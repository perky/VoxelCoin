package ljdp.voxelcoin.common.command;

import ljdp.voxelcoin.common.VoxelCoinMain;
import ljdp.voxelcoin.server.database.DBOAccount;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

public class BalanceCommand extends CommandBase {

	@Override
	public String getCommandName() {
		return "balance";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		EntityPlayer player = (EntityPlayer) sender;
		DBOAccount account = VoxelCoinMain.database.getPlayerAccount(player);
		sender.sendChatToPlayer("Your balance is " + account.balance + ".");
	}
	
	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "/balance";
	}
	

}
