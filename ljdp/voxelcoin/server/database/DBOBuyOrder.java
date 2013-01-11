package ljdp.voxelcoin.server.database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "buyorders")
public class DBOBuyOrder extends DBOOrder {
	
	public static final String MINQUANTITY_FIELD = "minQuantity";
	
	@DatabaseField(columnName = MINQUANTITY_FIELD)
	public int minQuantity;
	
	public DBOBuyOrder() {
		
	}
	
	public DBOBuyOrder(DBOItem item, DBOAccount account, DBOMarket market, int quantity, int price) {
		super(item, account, market, quantity, price);
		this.minQuantity = 1;
	}
	
	public int getTotalCost() {
		return quantity * price;
	}
	
}
