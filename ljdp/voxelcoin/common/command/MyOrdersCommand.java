package ljdp.voxelcoin.common.command;

import ljdp.voxelcoin.common.VoxelCoinMain;
import ljdp.voxelcoin.server.database.DBOAccount;
import ljdp.voxelcoin.server.database.DBOBuyOrder;
import ljdp.voxelcoin.server.database.DBOSellOrder;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

public class MyOrdersCommand extends CommandBase {

	@Override
	public String getCommandName() {
		return "myorders";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		if(args.length != 1)
			return;
		String type = args[0];
		if(type.equals("buy"))
			myBuyOrders(sender);
		else if(type.equals("sell"))
			mySellOrders(sender);
	}
	
	private void myBuyOrders(ICommandSender sender) {
		DBOAccount account = VoxelCoinMain.database.getPlayerAccount((EntityPlayer)sender);
		for(DBOBuyOrder buyOrder : account.buyOrders) {
			sender.sendChatToPlayer(buyOrder.prettyPrint());
		}
	}
	
	private void mySellOrders(ICommandSender sender) {
		DBOAccount account = VoxelCoinMain.database.getPlayerAccount((EntityPlayer)sender);
		for(DBOSellOrder sellOrder : account.sellOrders) {
			sender.sendChatToPlayer(sellOrder.prettyPrint());
		}
	}

}
