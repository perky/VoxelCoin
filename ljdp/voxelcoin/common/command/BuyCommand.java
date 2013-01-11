package ljdp.voxelcoin.common.command;

import java.sql.SQLException;
import java.util.ArrayList;

import ljdp.voxelcoin.common.VoxelCoinMain;
import ljdp.voxelcoin.server.database.DBOBuyOrder;
import ljdp.voxelcoin.server.database.DBOSellOrder;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class BuyCommand extends CommandBase {

	@Override
	public String getCommandName() {
		return "buy";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		if(args.length == 2) {
			buyDirect(sender, Integer.valueOf(args[0]), Integer.valueOf(args[1]));
		} else if(args.length == 4) {
			addBuyOrder(sender, 
					Integer.valueOf(args[0]), 
					Integer.valueOf(args[1]),
					Integer.valueOf(args[2]),
					Integer.valueOf(args[3])
			);
		}
	}

	private void buyDirect(ICommandSender sender, int orderID, int quantity) {
		EntityPlayer player = (EntityPlayer) sender;
		try {
			VoxelCoinMain.database.buyFromSellOrder(orderID, quantity, player);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void addBuyOrder(ICommandSender sender, int itemID, int itemDmg, int quantity, int price) {
		EntityPlayer player = (EntityPlayer) sender;
		ItemStack itemstack = new ItemStack(itemID, quantity, itemDmg);
		VoxelCoinMain.database.addBuyOrder(itemstack, player, quantity, price);
	}
	
	@Override
	public String getCommandUsage(ICommandSender par1iCommandSender) {
		return "/buy <sellOrderID quantity> OR <itemID itemDMG quantity price>";
	}

}
