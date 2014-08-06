package com.cokoc.fancyroulette;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ShopListener implements Listener {
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerClickShop(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Block block = event.getClickedBlock();
			if(block.getType() == Material.WALL_SIGN || block.getType() == Material.SIGN_POST) {
				Sign sign = (Sign) block.getState();
				String[] lines = sign.getLines();
				if(lines[0].equalsIgnoreCase("§6§l[Tokens]")) {
					String exchangeLine = lines[1];
					String tokenWorthString = exchangeLine.substring(0, exchangeLine.indexOf(':'));
					String numberOfTokensString = exchangeLine.substring(exchangeLine.indexOf(':')+1);
					int tokensWorth = Integer.parseInt(tokenWorthString);
					int numberOfTokens = Integer.parseInt(numberOfTokensString);
					double cash = FancyRoulette.instance.economy.getBalance(event.getPlayer().getName());
					if(cash >= (tokensWorth)) {
						player.sendMessage(ChatColor.GREEN + "You bought " + ChatColor.WHITE + numberOfTokens + ChatColor.GREEN + " for $" + ChatColor.WHITE
								+ (tokensWorth) + ChatColor.GREEN + "!");
						ItemStack tokens = new ItemStack(Material.FLOWER_POT_ITEM, numberOfTokens);
						ItemMeta meta = tokens.getItemMeta();
						meta.setDisplayName(ChatColor.GOLD + "Roulette token");
						tokens.setItemMeta(meta);
						player.getInventory().addItem(tokens);
						FancyRoulette.instance.economy.withdrawPlayer(event.getPlayer().getName(), tokensWorth);
						player.updateInventory();
						player.updateInventory();
					} else {
						player.sendMessage(ChatColor.DARK_RED + "You don't have enough money to buy these tokens!");
					}
				}
			}
		} else if(event.getAction() == Action.LEFT_CLICK_BLOCK) {
			Block block = event.getClickedBlock();
			if(block.getType() == Material.WALL_SIGN || block.getType() == Material.SIGN_POST) {
				Sign sign = (Sign) block.getState();
				String[] lines = sign.getLines();
				if(lines[0].equalsIgnoreCase("§6§l[Tokens]")) {
					String exchangeLine = lines[1];
					String tokensWorthString = exchangeLine.substring(0, exchangeLine.indexOf(':'));
					String numberOfTokensString = exchangeLine.substring(exchangeLine.indexOf(':')+1);
					int tokensWorth = Integer.parseInt(tokensWorthString);
					int numberOfTokens = Integer.parseInt(numberOfTokensString);
					int tokensInInventoryId = player.getInventory().first(Material.FLOWER_POT_ITEM);
					if(tokensInInventoryId == -1) {
						player.sendMessage(ChatColor.DARK_RED + "You don't even have tokens!");
						return;
					}
					ItemStack tokensInInventory = player.getInventory().getItem(tokensInInventoryId);
					int numberOfTokensInInventory = tokensInInventory.getAmount();
					boolean isToken = false;
					if(tokensInInventory.getItemMeta() != null)
						if(tokensInInventory.getItemMeta().getDisplayName() != null)
							if(tokensInInventory.getItemMeta().getDisplayName().equals(ChatColor.GOLD + "Roulette token"))
								isToken = true;
					if(! isToken)
						return;
					if(numberOfTokensInInventory >= numberOfTokens) {
						ItemStack tokens = new ItemStack(Material.FLOWER_POT_ITEM, numberOfTokens);
						ItemMeta meta = tokens.getItemMeta();
						meta.setDisplayName(ChatColor.GOLD + "Roulette token");
						tokens.setItemMeta(meta);
						if(isToken)
							player.getInventory().removeItem(tokens);
						else
							player.getInventory().removeItem(new ItemStack(Material.FLOWER_POT_ITEM, numberOfTokens));
						FancyRoulette.instance.economy.depositPlayer(player.getName(), tokensWorth);
						player.sendMessage(ChatColor.GREEN + "You sold " + ChatColor.WHITE + numberOfTokens + ChatColor.GREEN + " tokens for $" + ChatColor.WHITE
								+ tokensWorth + ChatColor.GREEN + "!");
						player.updateInventory();
						player.updateInventory();
					} else
						player.sendMessage(ChatColor.DARK_RED + "You don't have a big enough stack of tokens to sell!");
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerCreateShop(SignChangeEvent event) {
		Player player = event.getPlayer();
		String[] lines = event.getLines();
		if(lines[0].equalsIgnoreCase("[Token Trade]") || lines[0].equalsIgnoreCase("[Tokens]")) {
			if(event.getPlayer().hasPermission("roulette.admin")) {
				event.setLine(0, "§6§l[Tokens]");
				event.setLine(2, "§2§lLEFT: SELL");
				event.setLine(3, "§c§lRIGHT: BUY");
				player.sendMessage("§aYou have created a token exchange!");
			} else {
				player.sendMessage(ChatColor.DARK_RED + "You don't have enough permissions to build such a shop!");
			}
		}
	}
}
