package ljdp.voxelcoin.common.network;

import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;
import ljdp.easypacket.EasyPacketDispatcher;
import ljdp.easypacket.EasyPacketHandler;

public class NetworkManager implements IPacketHandler {
	
	private static NetworkManager instance;
	
	public static NetworkManager getInstance() {
		return instance;
	}
	
	private EasyPacketDispatcher dispatcher;
	public EasyPacketHandler requestSellOrdersHandler;
	
	public NetworkManager() {
		instance = this;
		dispatcher = new EasyPacketDispatcher("blockcoin");
		requestSellOrdersHandler = EasyPacketHandler.registerEasyPacket(PacketRequestSellOrders.class, dispatcher);
	}

	@Override
	public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player) {
		dispatcher.onPacketData(manager, packet, player);
	}
}
