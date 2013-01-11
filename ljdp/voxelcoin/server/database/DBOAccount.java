package ljdp.voxelcoin.server.database;

import ljdp.voxelcoin.common.util.Util;
import net.minecraft.entity.player.EntityPlayer;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;


@DatabaseTable(tableName = "accounts")
public class DBOAccount extends DatabaseObject {
	
	public static final String NAME_FIELD = "playerName";
	public static final String BALANCE_FIELD = "balance";
	
	@DatabaseField(canBeNull = false, unique = true, columnName = NAME_FIELD)
	public String playerName;
	
	@DatabaseField(columnName = BALANCE_FIELD)
	public int balance = 1000;
	
	@ForeignCollectionField(eager = false)
	public ForeignCollection<DBOSellOrder> sellOrders;
	
	@ForeignCollectionField(eager = false)
	public ForeignCollection<DBOBuyOrder> buyOrders;
	
	@ForeignCollectionField(eager = false, foreignFieldName = "buyingAccount")
	public ForeignCollection<DBOTransactionLog> buyTransactionLogs;
	
	@ForeignCollectionField(eager = false, foreignFieldName = "sellingAccount")
	public ForeignCollection<DBOTransactionLog> sellTransactionLogs;
	
	public DBOAccount() {
		
	}
	
	public DBOAccount(String playerName) {
		this.playerName = playerName;
	}
	
	public DBOAccount(EntityPlayer entityPlayer) {
		this.playerName = entityPlayer.getEntityName();
	}
	
	public EntityPlayer getEntityPlayer() {
		return Util.getPlayerFromName(playerName);
	}
	
}
