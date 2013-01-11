package ljdp.voxelcoin.common.command;

import ljdp.voxelcoin.common.VoxelCoinMain;
import ljdp.voxelcoin.server.database.DBOAccount;
import ljdp.voxelcoin.server.database.DBOTransactionLog;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;

public class TransactionLogCommand extends CommandBase {

	@Override
	public String getCommandName() {
		return "transactionlog";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		if(args.length < 1)
			return;
		if(args[0].equals("buy")) {
			if(args.length == 2)
				buyTransactionLog(sender, args[1]);
			else
				buyTransactionLog(sender);
		} else if(args[1].equals("sell")) {
			if(args.length == 2)
				sellTransactionLog(sender, args[1]);
			else
				sellTransactionLog(sender);
		}
	}
	
	private void buyTransactionLog(ICommandSender sender, String playerName) {
		DBOAccount account = VoxelCoinMain.database.getPlayerAccount(playerName);
		if(account.buyTransactionLogs == null)
			return;
		for(DBOTransactionLog log : account.buyTransactionLogs) {
			String msg = String.format("%s bought %d %s for %d from %s at %s.",
					log.buyingAccount.playerName,
					log.quantity,
					log.item.getItemStack(1).getDisplayName(),
					log.price,
					log.sellingAccount.playerName,
					log.timeCreated.toString()
					);
			sender.sendChatToPlayer(msg);
		}
	}
	
	private void buyTransactionLog(ICommandSender sender) {
		buyTransactionLog(sender, sender.getCommandSenderName());
	}
	
	private void sellTransactionLog(ICommandSender sender, String playerName) {
		DBOAccount account = VoxelCoinMain.database.getPlayerAccount(playerName);
		if(account.buyTransactionLogs == null)
			return;
		for(DBOTransactionLog log : account.buyTransactionLogs) {
			String msg = String.format("%s sold %d %s for %d to %s at %s.",
					log.sellingAccount.playerName,
					log.quantity,
					log.item.getItemStack(1).getDisplayName(),
					log.price,
					log.buyingAccount.playerName,
					log.timeCreated.toString()
					);
			sender.sendChatToPlayer(msg);
		}
	}
	
	private void sellTransactionLog(ICommandSender sender) {
		sellTransactionLog(sender, sender.getCommandSenderName());
	}
	
	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "/transactionlog <buy|sell> [playername]";
	}

}
