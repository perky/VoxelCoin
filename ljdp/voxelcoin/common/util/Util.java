package ljdp.voxelcoin.common.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldServer;
import cpw.mods.fml.common.FMLCommonHandler;

public class Util {
	
	public static EntityPlayer getPlayerFromName(String playerName) {
		if(FMLCommonHandler.instance().getMinecraftServerInstance() == null)
			return null;
		WorldServer[] servers = FMLCommonHandler.instance().getMinecraftServerInstance().worldServers;
		for(WorldServer server : servers) {
			if(server != null) {
				EntityPlayer player = server.getPlayerEntityByName(playerName);
				if(player != null)
					return player;
			}
		}
		return null;
	}
	
	public static byte[] tagCompoundToByteArray(NBTTagCompound nbt) {
		byte[] byteArray = null;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos      = new DataOutputStream(bos);
		try {
			NBTTagCompound.writeNamedTag(nbt, dos);
			byteArray = bos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return byteArray;
	}
	
	public static NBTTagCompound byteArrayToTagCompound(byte[] byteArray) {
		NBTTagCompound nbt = null;
		InputStream in      = new ByteArrayInputStream(byteArray);
		DataInputStream din = new DataInputStream(in);
		try {
			nbt = (NBTTagCompound) NBTTagCompound.readNamedTag(din);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return nbt;
	}
	
}
