package ljdp.voxelcoin.server.database;

import java.nio.file.Path;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ljdp.voxelcoin.server.database.datatype.NBTTagCompoundPersister;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Tuple;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.field.DataPersisterManager;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.PreparedDelete;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTableConfig;
import com.j256.ormlite.table.TableUtils;

public class Database {
	
	ConnectionSource connection;
	
	private Dao<DBOAccount, Integer> accountHandler;
	private Dao<DBOItem, Integer> itemHandler;
	private Dao<DBOBuyOrder, Integer> buyOrderHandler;
	private Dao<DBOSellOrder, Integer> sellOrderHandler;
	private Dao<DBOMarket, Integer> marketHandler;
	private Dao<DBOTransactionLog, Integer> transactionLogHandler;
	
	public Database(Path databasePath) throws ClassNotFoundException {
	    connection = null;
	    try {
	    	connection = new JdbcConnectionSource("jdbc:sqlite:" + databasePath.toString());
	    	setupDatabase(connection);
	    } catch(SQLException e) {
	    	System.err.println(e.getMessage());
	    }
	}
	
	private void setupDatabase(ConnectionSource connection) throws SQLException {
		DataPersisterManager.registerDataPersisters(NBTTagCompoundPersister.getSingleton());
		
		accountHandler = DaoManager.createDao(connection, DBOAccount.class);
		itemHandler = DaoManager.createDao(connection, DBOItem.class);
		buyOrderHandler = DaoManager.createDao(connection, DBOBuyOrder.class);
		sellOrderHandler = DaoManager.createDao(connection, DBOSellOrder.class);
		marketHandler = DaoManager.createDao(connection, DBOMarket.class);
		transactionLogHandler = DaoManager.createDao(connection, DBOTransactionLog.class);
		TableUtils.createTableIfNotExists(connection, DBOAccount.class);
		TableUtils.createTableIfNotExists(connection, DBOItem.class);
		TableUtils.createTableIfNotExists(connection, DBOBuyOrder.class);
		TableUtils.createTableIfNotExists(connection, DBOSellOrder.class);
		TableUtils.createTableIfNotExists(connection, DBOMarket.class);
		TableUtils.createTableIfNotExists(connection, DBOTransactionLog.class);
		String query = "CREATE TRIGGER IF NOT EXISTS onSellOrderEmpty AFTER UPDATE OF %FIELD% ON %TABLE% " +
						"FOR EACH ROW WHEN new.%FIELD% <= 0 BEGIN " +
						"DELETE FROM %TABLE% WHERE id = new.id; " +
						"END;";
		query = query.replaceAll("%TABLE%", DatabaseTableConfig.extractTableName(DBOSellOrder.class));
		query = query.replaceAll("%FIELD%", "quantity");
		sellOrderHandler.updateRaw(query);
	}
	
	public void addSellOrder(ItemStack itemstack, EntityPlayer entityPlayer, int quantity, int price) {
		DBOAccount account = getPlayerAccount(entityPlayer);
		DBOMarket market   = getMarket("MockMarket");
		DBOItem item = new DBOItem(itemstack);
		DBOSellOrder sellOrder = new DBOSellOrder(item, account, market, quantity, price);
		try {
			List<DBOBuyOrder> matchingBuyOrders = getMatchingBuyOrders(sellOrder);
			doSellBuyTransaction(sellOrder, matchingBuyOrders);
			if(sellOrder.quantity > 0) {
				itemHandler.create(item);
				sellOrderHandler.create(sellOrder);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void addBuyOrder(ItemStack itemstack, EntityPlayer entityPlayer, int quantity, int price) {
		DBOAccount account = getPlayerAccount(entityPlayer);
		if(account.balance < quantity * price)
			return;
		DBOMarket market   = getMarket("MockMarket");
		DBOItem item = new DBOItem(itemstack);
		DBOBuyOrder buyOrder = new DBOBuyOrder(item, account, market, quantity, price);
		try {
			List<DBOSellOrder> matchingSellOrders = getMatchingSellOrders(buyOrder);
			doBuySellTransaction(buyOrder, matchingSellOrders);
			if(buyOrder.quantity > 0) {
				itemHandler.create(item);
				buyOrderHandler.create(buyOrder);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void doBuySellTransaction(DBOBuyOrder buyOrder, List<DBOSellOrder> matchingSellOrders) throws SQLException {
		int buyOrderQuantity = buyOrder.quantity;
		int quantityBought = 0;
		int totalCost = 0;
		List<DBOTransactionLog> logs = new ArrayList();
		for(DBOSellOrder sellOrder : matchingSellOrders) {
			// We have to iterate through the whole list!
			if(buyOrderQuantity > 0) {
				int quantityToTake = Math.min(buyOrderQuantity, sellOrder.quantity);
				int cost = sellOrder.price * quantityToTake;
				sellOrder.quantity -= quantityToTake;
				sellOrder.account.balance += cost;
				quantityBought   += quantityToTake;
				buyOrderQuantity -= quantityToTake;
				totalCost        += cost;
				DBOTransactionLog log = createTransactionLog(buyOrder, sellOrder, quantityToTake, cost);
				logs.add(log);
			}
		}
		if(quantityBought >= buyOrder.minQuantity) {
			givePlayerOrderItem(buyOrder.account, buyOrder.item, quantityBought);
			buyOrder.quantity = buyOrderQuantity;
			buyOrder.account.balance -= totalCost;
			for(DBOTransactionLog log : logs)
				transactionLogHandler.create(log);
			for(DBOSellOrder sellOrder : matchingSellOrders)
				sellOrderHandler.update(sellOrder);
		}
	}
	
	private void doSellBuyTransaction(DBOSellOrder sellOrder, List<DBOBuyOrder> matchingBuyOrders) throws SQLException {
		int sellOrderQuantity = sellOrder.quantity;
		int quantitySold = 0;
		int totalCost = 0;
		List<DBOTransactionLog> logs = new ArrayList();
		for(DBOBuyOrder buyOrder : matchingBuyOrders) {
			// We have to iterate through the whole list!
			if(sellOrderQuantity > 0 && buyOrder.minQuantity >= sellOrderQuantity) {
				int quantityToSell = Math.min(sellOrderQuantity, sellOrderQuantity);
				int cost = buyOrder.price * quantityToSell;
				buyOrder.quantity -= quantityToSell;
				buyOrder.account.balance -= cost;
				quantitySold += quantityToSell;
				totalCost += cost;
				sellOrderQuantity -= quantityToSell;
				givePlayerOrderItem(buyOrder.account, buyOrder.item, quantityToSell);
				DBOTransactionLog log = createTransactionLog(buyOrder, sellOrder, quantityToSell, cost);
				logs.add(log);
			}
		}
		if(quantitySold > 0) {
			sellOrder.quantity = sellOrderQuantity;
			sellOrder.account.balance += totalCost;
			for(DBOTransactionLog log : logs)
				transactionLogHandler.create(log);
			for(DBOBuyOrder buyOrder : matchingBuyOrders)
				buyOrderHandler.update(buyOrder);
		}
	}
	
	private void givePlayerOrderItem(DBOAccount account, DBOItem item, int quantity) {
		ItemStack itemstack = item.getItemStack(quantity);
		EntityPlayer player = account.getEntityPlayer();
		if(player != null)
			player.inventory.addItemStackToInventory(itemstack);
	}

	private List<DBOSellOrder> getMatchingSellOrders(DBOBuyOrder buyOrder) throws SQLException {
		QueryBuilder<DBOSellOrder, Integer> sellOrderQuery = sellOrderHandler.queryBuilder();
		QueryBuilder<DBOItem, Integer> itemQuery = getMatchingItemQuery(buyOrder);
		sellOrderQuery.where().le(DBOOrder.PRICE_FIELD, buyOrder.price);
		sellOrderQuery.join(itemQuery).orderBy(DBOOrder.PRICE_FIELD, true);
		return sellOrderQuery.query();
	}
	
	private List<DBOBuyOrder> getMatchingBuyOrders(DBOSellOrder sellOrder) throws SQLException {
		QueryBuilder<DBOBuyOrder, Integer> buyOrderQuery = buyOrderHandler.queryBuilder();
		QueryBuilder<DBOItem, Integer> itemQuery = getMatchingItemQuery(sellOrder);
		buyOrderQuery.where().ge(DBOOrder.PRICE_FIELD, sellOrder.price);
		buyOrderQuery.join(itemQuery).orderBy(DBOOrder.PRICE_FIELD, false);
		return buyOrderQuery.query();
	}
	
	private QueryBuilder<DBOItem, Integer> getMatchingItemQuery(DBOOrder order) throws SQLException {
		QueryBuilder<DBOItem, Integer> itemQuery = itemHandler.queryBuilder();
		Where<DBOItem, Integer> where = itemQuery.where();
		if(order.item.tagCompound == null) {
			where.and(
					where.eq(DBOItem.ITEMID_FIELD, order.item.itemID),
					where.eq(DBOItem.ITEMMDG_FIELD, order.item.itemDmg),
					where.isNull(DBOItem.ITEMTAG_FIELD)
			);
		} else {
			where.and(
					where.eq(DBOItem.ITEMID_FIELD, order.item.itemID),
					where.eq(DBOItem.ITEMMDG_FIELD, order.item.itemDmg),
					where.eq(DBOItem.ITEMTAG_FIELD, order.item.tagCompound)
			);
		}
		
		return itemQuery;
	}
	
	private DBOTransactionLog createTransactionLog(DBOBuyOrder buyOrder, DBOSellOrder sellOrder, int quantity, int price) {
		DBOMarket market = sellOrder.market;
		DBOItem item = sellOrder.item;
		DBOAccount sellingAccount = sellOrder.account;
		DBOAccount buyingAccount  = buyOrder.account;
		DBOTransactionLog log = new DBOTransactionLog(sellingAccount, buyingAccount, item, market, quantity, price);
		return log;
	}
	
	public List<DBOBuyOrder> getAllBuyOrders() {
		try {
			return buyOrderHandler.queryForAll();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return new ArrayList();
	}
	
	public List<DBOSellOrder> getAllSellOrders() {
		try {
			return sellOrderHandler.queryForAll();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return new ArrayList();
	}
	
	public void buyFromSellOrder(int id, int quantityToBuy, EntityPlayer player) throws SQLException {
		DBOAccount buyingPlayer = getPlayerAccount(player);
		DBOSellOrder sellOrder = getSellOrderByID(id);
		if(sellOrder != null && sellOrder.quantity >= quantityToBuy) {
			int totalCost = sellOrder.price * quantityToBuy;
			if(buyingPlayer.balance >= totalCost) {
				sellOrder.account.balance += totalCost;
				sellOrder.quantity -= quantityToBuy;
				sellOrderHandler.update(sellOrder);
				accountHandler.update(sellOrder.account);
				buyingPlayer.balance -= totalCost;
				accountHandler.update(buyingPlayer);
				givePlayerOrderItem(buyingPlayer, sellOrder.item, quantityToBuy);
			}
		}
	}
	
	public DBOSellOrder getSellOrderByID(int id) {
		try {
			return sellOrderHandler.queryForId(id);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public DBOAccount getPlayerAccount(EntityPlayer player) {
		return getPlayerAccount(player.getEntityName());
	}
	
	public DBOAccount getPlayerAccount(String playerName) {
		QueryBuilder<DBOAccount, Integer> qb = accountHandler.queryBuilder();
		DBOAccount account = null;
		try {
			qb.where().eq(DBOAccount.NAME_FIELD, playerName);
			account = qb.queryForFirst();
			if(account == null) {
				account = new DBOAccount(playerName);
				accountHandler.create(account);
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		return account;
	}
	
	public DBOMarket getMarket(String marketName) {
		QueryBuilder<DBOMarket, Integer> qb = marketHandler.queryBuilder();
		DBOMarket market = null;
		try {
			qb.where().eq(DBOMarket.NAME_FIELD, marketName);
			market = qb.queryForFirst();
			if(market == null) {
				market = new DBOMarket(marketName, 0, 0, 0);
				marketHandler.create(market);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return market;
	}
	
	public void cancelAllBuyOrders(EntityPlayer player) {
		DBOAccount account = getPlayerAccount(player);
		if(account.buyOrders != null) {
			try {
				for(DBOBuyOrder buyOrder : account.buyOrders) {
					account.balance += buyOrder.getTotalCost();
				}
				accountHandler.update(account);
				buyOrderHandler.delete(account.buyOrders);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void cancelBuyOrder(EntityPlayer player, int id) {
		DBOAccount account = getPlayerAccount(player);
		QueryBuilder<DBOBuyOrder, Integer> qb = buyOrderHandler.queryBuilder();
		try {
			qb.where()
			.eq(DBOOrder.ACCOUNT_FIELD, account.id)
			.and()
			.eq("id", id);
			DBOBuyOrder buyOrder = qb.queryForFirst();
			if(buyOrder != null) {
				account.balance += buyOrder.getTotalCost();
				accountHandler.update(account);
				buyOrderHandler.delete(buyOrder);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void cancelAllSellOrders(EntityPlayer player) {
		DBOAccount account = getPlayerAccount(player);
		if(account.sellOrders != null) {
			try {
				for(DBOSellOrder sellOrder : account.sellOrders) {
					givePlayerOrderItem(account, sellOrder.item, sellOrder.quantity);
				}
				sellOrderHandler.delete(account.sellOrders);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void cancelSellOrder(EntityPlayer player, int id) {
		DBOAccount account = getPlayerAccount(player);
		QueryBuilder<DBOSellOrder, Integer> qb = sellOrderHandler.queryBuilder();
		try {
			qb.where()
			.eq(DBOOrder.ACCOUNT_FIELD, account.id)
			.and()
			.eq("id", id);
			DBOSellOrder sellOrder = qb.queryForFirst();
			if(sellOrder != null) {
				givePlayerOrderItem(account, sellOrder.item, sellOrder.quantity);
				sellOrderHandler.delete(sellOrder);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
}
