package ljdp.voxelcoin.server.database;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "markets")
public class DBOMarket extends DatabaseObject {
	
	public static final String NAME_FIELD = "name";
	
	@DatabaseField(unique = true, columnName = NAME_FIELD)
	public String name;
	
	@DatabaseField
	public int xCoord;
	
	@DatabaseField
	public int yCoord;
	
	@DatabaseField
	public int zCoord;
	
	public DBOMarket() {
		
	}
	
	public DBOMarket(String name, int x, int y, int z) {
		this.name = name;
		this.xCoord = x;
		this.yCoord = y;
		this.zCoord = z;
	}
	
}
