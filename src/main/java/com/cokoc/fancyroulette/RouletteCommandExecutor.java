package cokoc.fancyroulette;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RouletteCommandExecutor implements CommandExecutor {
	public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
		if(! (commandSender instanceof Player)) {
			commandSender.sendMessage(ChatColor.DARK_RED + "You need to be a player to use this command!");
			return true;
		} Player senderPlayer = (Player) commandSender;
		if(! (senderPlayer.hasPermission("roulette.admin"))) {
			commandSender.sendMessage(ChatColor.DARK_RED + "You don't have perms to use this command!");
			return true;
		}

		if(args.length == 0) {
			senderPlayer.sendMessage(ChatColor.DARK_RED + "Error! " + ChatColor.WHITE + "Not enough arguments!");
			senderPlayer.sendMessage("Try /roulette [add/remove/clear]");
			return true;
		}

		String subCommand = args[0];

		if(subCommand.equalsIgnoreCase("add"))
			onAdd(senderPlayer, args);
		if(subCommand.equalsIgnoreCase("remove"))
			onRemove(senderPlayer, args);
		if(subCommand.equalsIgnoreCase("clear"))
			onClear(senderPlayer, args);

		return true;
	}

	public void onAdd(Player senderPlayer, String[] args) {
		Block targetBlock = senderPlayer.getTargetBlock(null, 100);
		if(targetBlock == null) {
			senderPlayer.sendMessage(ChatColor.DARK_RED + "You must be looking at a block while using this command!");
			return;
		}

		if(args.length < 2) {
			senderPlayer.sendMessage(ChatColor.DARK_RED + "Error! " + ChatColor.WHITE + "Not enough arguments!");
			senderPlayer.sendMessage("You must input which roulette you want to add this block to, try /roulette add 1");
		}

		if(args.length > 2) {
			int tableId = Integer.parseInt(args[1]);
			FancyRoulette.instance.tableManager.setTableSpinner(tableId, targetBlock);
			senderPlayer.sendMessage("You have set this block as the spinner for table id " + ChatColor.AQUA + tableId);
		} else {
			int tableId = Integer.parseInt(args[1]);
			FancyRoulette.instance.tableManager.addBlockToTable(tableId, targetBlock);
			senderPlayer.sendMessage("You have added this block to the table id " + ChatColor.AQUA + tableId);
		}
	}

	public void onRemove(Player senderPlayer, String[] args) {
		Block targetBlock = senderPlayer.getTargetBlock(null, 100);
		if(targetBlock == null) {
			senderPlayer.sendMessage(ChatColor.DARK_RED + "You must be looking at a block while using this command!");
			return;
		}

		if(FancyRoulette.instance.tableManager.removeBlockFromRoulette(targetBlock))
			senderPlayer.sendMessage("You removed this block from the roulette.");
		else
			senderPlayer.sendMessage("This block isn't even in a roulette!");
	}

	public void onClear(Player senderPlayer, String[] args) {
		if(args.length < 2) {
			senderPlayer.sendMessage(ChatColor.DARK_RED + "Error! " + ChatColor.WHITE + "Not enough arguments!");
			senderPlayer.sendMessage("You must input which roulette you want to clear, try /roulette clear 1");
		}
		
		int tableId = Integer.parseInt(args[1]);
		FancyRoulette.instance.tableManager.clearTableTiles(tableId);
		senderPlayer.sendMessage("You have cleared the table!");
	}
}
