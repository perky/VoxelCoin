package ljdp.voxelcoin.common.command;

import java.util.ArrayList;
import java.util.List;

import ljdp.voxelcoin.common.VoxelCoinMain;
import ljdp.voxelcoin.server.database.DBOBuyOrder;
import ljdp.voxelcoin.server.database.DBOSellOrder;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;

public class ListOrdersCommand extends CommandBase {

	@Override
	public String getCommandName() {
		return "orders";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		if(args.length < 1)
			return;
		if(args[0].equals("buy")) {
			showBuyOrders(sender);
		} else if(args[0].equals("sell")) {
			showSellOrders(sender);
		}
	}
	
	private void showBuyOrders(ICommandSender sender) {
		List<DBOBuyOrder> buyOrders = VoxelCoinMain.database.getAllBuyOrders();
		for(DBOBuyOrder buyOrder : buyOrders) {
			sender.sendChatToPlayer(buyOrder.prettyPrint());
		}
	}

	private void showSellOrders(ICommandSender sender) {
		List<DBOSellOrder> sellOrders = VoxelCoinMain.database.getAllSellOrders();
		for(DBOSellOrder sellOrder: sellOrders) {
			sender.sendChatToPlayer(sellOrder.prettyPrint());
		}
	}
	
	@Override
	public String getCommandUsage(ICommandSender par1iCommandSender) {
		return "/listorders <buy|sell>";
	}

}
