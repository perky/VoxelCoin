package ljdp.voxelcoin.common.command;

import ljdp.voxelcoin.common.VoxelCoinMain;
import ljdp.voxelcoin.server.database.DBOBuyOrder;
import ljdp.voxelcoin.server.database.DBOSellOrder;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

public class CancelOrderCommand extends CommandBase {

	@Override
	public String getCommandName() {
		return "cancelorder";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		if(args.length != 2)
			return;
		EntityPlayer player = (EntityPlayer) sender;
		if(args[0].equals("sell")) {
			if(args[1].equals("ALL"))
				VoxelCoinMain.database.cancelAllSellOrders(player);
			else
				VoxelCoinMain.database.cancelSellOrder(player, Integer.valueOf(args[1]));
		} else if(args[0].equals("buy")) {
			if(args[1].equals("ALL"))
				VoxelCoinMain.database.cancelAllBuyOrders(player);
			else
				VoxelCoinMain.database.cancelBuyOrder(player, Integer.valueOf(args[1]));
		}
	}
	
	@Override
	public String getCommandUsage(ICommandSender par1iCommandSender) {
		return "/cancelorder <buy|sell> <orderID|ALL>";
	}

}
