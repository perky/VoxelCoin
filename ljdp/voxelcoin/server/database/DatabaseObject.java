package ljdp.voxelcoin.server.database;

import com.j256.ormlite.field.DatabaseField;

public abstract class DatabaseObject {
	
	@DatabaseField(generatedId = true)
	public int id;
	
}
