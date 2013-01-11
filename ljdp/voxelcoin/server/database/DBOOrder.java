package ljdp.voxelcoin.server.database;

import com.j256.ormlite.field.DatabaseField;

public abstract class DBOOrder extends DatabaseObject {
	
	public static final String QUANTITY_FIELD = "quantity";
	public static final String PRICE_FIELD = "price";
	public static final String ACCOUNT_FIELD = "account_id";
	
	@DatabaseField(columnName = QUANTITY_FIELD)
	public int quantity;
	
	@DatabaseField(columnName = PRICE_FIELD)
	public int price;
	
	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	public DBOItem item;
	
	@DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = ACCOUNT_FIELD)
	public DBOAccount account;
	
	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	public DBOMarket market;
	
	public DBOOrder(DBOItem item, DBOAccount account, DBOMarket market, int quantity, int price) {
		this.item = item;
		this.account = account;
		this.market = market;
		this.quantity = quantity;
		this.price = price;
	}
	
	public DBOOrder() {
		
	}
	
	public String prettyPrint() {
		String str = String.format("%d. %s is %s %s x %d for %d each.",
				id,
				account.playerName,
				(this instanceof DBOBuyOrder) ? "buying" : "selling",
				item.getItemStack(1).getDisplayName(),
				quantity,
				price
		);
		return str;
	}

}
