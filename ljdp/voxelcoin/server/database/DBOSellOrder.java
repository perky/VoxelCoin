package ljdp.voxelcoin.server.database;

import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "sellorders")
public class DBOSellOrder extends DBOOrder {
	
	public DBOSellOrder() {	
	}
	
	public DBOSellOrder(DBOItem item, DBOAccount account, DBOMarket market, int quantity, int price) {
		super(item, account, market, quantity, price);
	}

}
