package ljdp.voxelcoin.server.database;

import java.sql.Date;
import java.sql.Timestamp;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "transactionlogs")
public class DBOTransactionLog extends DatabaseObject {
	
	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	public DBOAccount sellingAccount;
	
	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	public DBOAccount buyingAccount;
	
	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	public DBOItem item;
	
	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	public DBOMarket market;
	
	@DatabaseField
	public int quantity;
	
	@DatabaseField
	public int price;
	
	@DatabaseField
	public Date timeCreated;
	
	public DBOTransactionLog() {
	}
	
	public DBOTransactionLog(DBOAccount sellingAccount, DBOAccount buyingAccount, 
			DBOItem item, DBOMarket market, int quantity, int price)
	{
		this.sellingAccount = sellingAccount;
		this.buyingAccount = buyingAccount;
		this.item = item;
		this.market = market;
		this.quantity = quantity;
		this.price = price;
		java.util.Date date = new java.util.Date();
		this.timeCreated = new Date(date.getTime());
	}
	
}
