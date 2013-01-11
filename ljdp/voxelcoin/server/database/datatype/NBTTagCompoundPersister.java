package ljdp.voxelcoin.server.database.datatype;

import java.io.IOException;
import java.sql.SQLException;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;

import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.field.types.*;
import com.j256.ormlite.support.DatabaseResults;

public class NBTTagCompoundPersister extends ByteArrayType {
	
	private static NBTTagCompoundPersister instance = new NBTTagCompoundPersister();
	
	private NBTTagCompoundPersister () {
		super(SqlType.BYTE_ARRAY, new Class<?>[] { NBTTagCompound.class });
	}
	
	public static NBTTagCompoundPersister getSingleton() {
		return instance;
	}
	
	@Override
	public Object javaToSqlArg(FieldType fieldType, Object javaObject) throws SQLException {
		NBTTagCompound tagCompound = (NBTTagCompound) javaObject;
		if(tagCompound == null) {
			return null;
		} else {
			byte[] byteArray = null;
			try {
				byteArray = CompressedStreamTools.compress(tagCompound);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return byteArray;
		}
	}
	
	@Override
	public Object sqlArgToJava(FieldType fieldType, Object sqlArg, int columnPos) throws SQLException {
		byte[] compressedTag = (byte[]) sqlArg;
		NBTTagCompound tagCompound = null;
		try {
			tagCompound = CompressedStreamTools.decompress(compressedTag);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return tagCompound;
	}
	
}
