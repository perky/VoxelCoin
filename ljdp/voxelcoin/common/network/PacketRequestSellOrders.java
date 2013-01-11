package ljdp.voxelcoin.common.network;

import cpw.mods.fml.common.network.Player;
import ljdp.easypacket.EasyPacket;
import ljdp.easypacket.EasyPacketData;

public class PacketRequestSellOrders extends EasyPacket {
	
	@EasyPacketData
	public int marketXCoord;
	
	@EasyPacketData
	public int marketYCoord;
	
	@EasyPacketData
	public int marketZCoord;
	
	@EasyPacketData
	public int limit;
	
	@EasyPacketData
	public int offset;

	@Override
	public boolean isChunkDataPacket() {
		return false;
	}

	@Override
	public void onReceive(Player player) {
		
	}
	
}
