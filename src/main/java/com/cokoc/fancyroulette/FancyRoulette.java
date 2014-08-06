package com.cokoc.fancyroulette;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class FancyRoulette extends JavaPlugin {
	public static FancyRoulette instance;
	public TableManager tableManager;
	public Economy economy;

	public void onEnable() {
		saveDefaultConfig();
		instance = this;
		tableManager = new TableManager();
		if(FileIO.checkFileCreate("/FancyRoulette/", "tileslocations.bin")) {
			FileIO.checkFileCreate("/FancyRoulette/", "spinnerslocations.bin");
			tableManager.loadData();
		}
		
		if(! getConfig().getBoolean("opt out of metrics")) {
			try {
				MetricsLite metrics = new MetricsLite(this);
				metrics.start();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
		if (economyProvider != null)
			economy = economyProvider.getProvider();

		getServer().getPluginManager().registerEvents(new PlayerListener(), this);
		getServer().getPluginManager().registerEvents(new ShopListener(), this);
	}
	
	public void onDisable() {
		tableManager.saveData();
	}
	
	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
		if(! (commandSender instanceof Player)) {
			commandSender.sendMessage(ChatColor.DARK_RED + "You need to be a player to use this command!");
			return true;
		} Player senderPlayer = (Player) commandSender;
		if(! (senderPlayer.hasPermission("roulette.admin"))) {
			commandSender.sendMessage(ChatColor.DARK_RED + "You don't have perms to use this command!");
			return true;
		} if(command.getName().equalsIgnoreCase("roulette")) {
			Block targetBlock = senderPlayer.getTargetBlock(null, 100);
			if(targetBlock == null) {
				senderPlayer.sendMessage(ChatColor.DARK_RED + "You must be looking at a block while using this command!");
				return true;
			} if(args.length == 1) {
				int tableId = Integer.parseInt(args[0]);
				tableManager.addBlockToTable(tableId, targetBlock);
				senderPlayer.sendMessage("You have added this block to the table id " + ChatColor.AQUA + tableId);
			} else {
				if(args.length == 2) {
					int tableId = Integer.parseInt(args[0]);
					tableManager.setTableSpinner(tableId, targetBlock);
					senderPlayer.sendMessage("You have set this block as the spinner for table id " + ChatColor.AQUA + tableId);
				} else {
					if(tableManager.removeBlockFromRoulette(targetBlock))
						senderPlayer.sendMessage("You removed this block from the roulette.");
					else
						senderPlayer.sendMessage("This block isn't in the roulette!");
				}
			}
		} return true;
	}
}
