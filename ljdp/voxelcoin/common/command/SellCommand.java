package ljdp.voxelcoin.common.command;

import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;

import ljdp.voxelcoin.common.VoxelCoinMain;
import ljdp.voxelcoin.server.database.DBOSellOrder;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class SellCommand extends CommandBase {

	@Override
	public String getCommandName() {
		return "sell";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		EntityPlayer player = (EntityPlayer) sender;
		ItemStack itemstack = player.inventory.getCurrentItem();
		if(args.length != 1 || itemstack == null) {
			sender.sendChatToPlayer("Sell order invalid.");
			return;
		}

		int price = Integer.valueOf(args[0]);
		if(itemstack != null) {
			itemstack = player.inventory.decrStackSize(player.inventory.currentItem, itemstack.stackSize);
			VoxelCoinMain.database.addSellOrder(itemstack, player, itemstack.stackSize, price);
			sender.sendChatToPlayer("Sell order created.");
		}
	}
	
	@Override
	public String getCommandUsage(ICommandSender par1iCommandSender) {
		return "/sell price";
	}

}
