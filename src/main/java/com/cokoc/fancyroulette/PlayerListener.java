package cokoc.fancyroulette;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Wool;

public class PlayerListener implements Listener {
	private BetsManager bets = new BetsManager();
	private ArrayList<Block> spinning = new ArrayList<Block>();
	FireworkEffectPlayer fire = new FireworkEffectPlayer();
	
	private boolean isToken(ItemStack itemStack) {
		if(itemStack == null)
			return false;
		if(! itemStack.getType().equals(Material.FLOWER_POT_ITEM))
			return false;
		if(itemStack.getItemMeta() == null)
			return false;
		if(itemStack.getItemMeta().getDisplayName() == null)
			return false;
		if(! itemStack.getItemMeta().getDisplayName().contains("Roulette token"))
			return false;
		return true;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public  void onPlayerBetInteract(PlayerInteractEvent event) {
		ItemStack itemInHand = event.getItem();
		if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			Block clickedBlock = event.getClickedBlock();
			if(! FancyRoulette.instance.tableManager.isBlockTile(clickedBlock))
				return;
			if(itemInHand == null) {
				event.getPlayer().sendMessage(ChatColor.GOLD + "You need to have roulette tokens to play the roulette!");
				return;
			} else if(! isToken(itemInHand)) {
				event.getPlayer().sendMessage(ChatColor.RED + "You can only place roulette tokens on roulette tiles!");
				return;
			} event.setCancelled(false); // Bypass shit like worldguard.
			int tableId = FancyRoulette.instance.tableManager.getTableId(clickedBlock);
			int maxNumberOfBets = FancyRoulette.instance.getConfig().getInt("maximum bets per player");
			if(bets.getNumberOfBets(event.getPlayer()) >= maxNumberOfBets) {
				event.getPlayer().sendMessage(ChatColor.RED + "You cannot have more bets than " + maxNumberOfBets + "!");
			} event.getPlayer().sendMessage(ChatColor.GOLD + "You placed a bet for " + ChatColor.BOLD + getWoolNameIfWool(clickedBlock)
					+ ChatColor.RESET + " " + ChatColor.GOLD + " on table " + ChatColor.AQUA + tableId + ChatColor.WHITE + "!");
			bets.addBet(event.getPlayer(), clickedBlock.getRelative(BlockFace.UP));
		}
	}
	
	@EventHandler (priority = EventPriority.HIGHEST)
	public void onPlayerRemoveBet(BlockBreakEvent event) {
		Block block = event.getBlock();
		if(block.getType().equals(Material.FLOWER_POT)) {
			Location blockLocation = block.getLocation();
			Location blockUnderLocation = blockLocation;
			blockUnderLocation.setY(blockLocation.getY() - 1);
			Block blockUnder = blockLocation.getWorld().getBlockAt(blockUnderLocation);
			if(FancyRoulette.instance.tableManager.isBlockTile(blockUnder)) {
				event.setCancelled(true);
				block.setType(Material.AIR);
				ItemStack flowerPots = new ItemStack(Material.FLOWER_POT_ITEM, 1);
				ItemMeta meta = flowerPots.getItemMeta();
				meta.setDisplayName(ChatColor.GOLD + "Roulette token");
				flowerPots.setItemMeta(meta);
				event.getPlayer().getInventory().addItem(flowerPots);
				int tableId = FancyRoulette.instance.tableManager.getTableId(blockUnder);
				event.getPlayer().sendMessage(ChatColor.GOLD + "You removed your bet for " + ChatColor.BOLD + getWoolNameIfWool(blockUnder)
						+ ChatColor.RESET + " " + ChatColor.GOLD + " on table " + ChatColor.AQUA + tableId + ChatColor.WHITE + "!");
				bets.removeBetForPlayer(event.getPlayer(), block);
			}
		}
	}	
	
	public void onSpinEnd(Wool winningColor, Block spinningBlock) {
		int tableId = FancyRoulette.instance.tableManager.getTableId(spinningBlock);
		ArrayList<SerializableLocation> tableTiles = FancyRoulette.instance.tableManager.getTableTiles(tableId);
		for(int i = 0; i < tableTiles.size(); ++i) {
			Location tileLocation = tableTiles.get(i).toLocation();
			Block wool = spinningBlock.getWorld().getBlockAt(tileLocation);
			Wool woolColor = new Wool(wool.getType(), wool.getData());
			Location betLocation = tileLocation;
			betLocation.setY(betLocation.getY() + 1);
			Block bet = tileLocation.getWorld().getBlockAt(betLocation);
			if(woolColor.equals(winningColor)) {
				if(bets.isBlockBettedOn(bet)) {
					Player better = bets.whoBettedOn(bet);
					if(better != null) {
						better.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "Congrats! " + ChatColor.WHITE + "You " + ChatColor.GREEN + "won " + ChatColor.WHITE + "your bet on " + getWoolNameIfWool(wool) + "!");
						ItemStack reward;
						if(FancyRoulette.instance.getConfig().getString("reward type").equals("Tokens")) {
							reward = new ItemStack(Material.FLOWER_POT_ITEM);
							ItemMeta meta = reward.getItemMeta();
							meta.setDisplayName(ChatColor.GOLD + "Roulette token");
							reward.setItemMeta(meta);
						} else {
							int rewardTypeId = FancyRoulette.instance.getConfig().getInt("reward type");
							reward = new ItemStack(Material.getMaterial(rewardTypeId));
						} int quantity = FancyRoulette.instance.getConfig().getInt("reward amount");
						reward.setAmount(quantity);
						better.getInventory().addItem(reward);
						FireworkEffect effect = FireworkEffect.builder().flicker(false).withColor(Color.WHITE).withColor(Color.AQUA).withColor(Color.BLACK).withColor(Color.BLUE)
								.withColor(Color.FUCHSIA).withColor(Color.GREEN).withColor(Color.LIME).withColor(Color.MAROON).withColor(Color.NAVY).withColor(Color.OLIVE)
								.withColor(Color.ORANGE).withColor(Color.PURPLE).withColor(Color.SILVER).withColor(Color.TEAL).withColor(Color.YELLOW).with(Type.BALL_LARGE).build();
						SerializableLocation spinnerLocation = FancyRoulette.instance.tableManager.getTableSpinner(tableId);
						Location explosionLocation = spinnerLocation.toLocation();
						explosionLocation.setY(explosionLocation.getY() + 3);
						try {
							fire.playFirework(explosionLocation, effect);
						} catch (Exception e) {
							e.printStackTrace();
						}
					} bets.removeBet(bet);
				} bet.setType(Material.AIR);
			} else {
				if(bets.isBlockBettedOn(bet)) {
					Player better = bets.whoBettedOn(bet);
					if(better != null) {
						better.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "Ho noes! " + ChatColor.WHITE + "You " + ChatColor.RED + "lost " + ChatColor.WHITE + "your bet on " + getWoolNameIfWool(wool) + "!");
					} bets.removeBet(bet);
				} bet.setType(Material.AIR);
			}
		}
	}
	
	@EventHandler
	public void onPlayerSpin(final PlayerInteractEvent event) {
		final Block block = event.getClickedBlock();
		if(block == null)
			return;
		if(FancyRoulette.instance.tableManager.isBlockSpinner(block)) {
			if(! event.getPlayer().hasPermission("roulette.spin")) {
				event.getPlayer().sendMessage(ChatColor.DARK_RED + "Error! " + ChatColor.WHITE + "You don't have enough permissions to spin the roulette!");
			} if(spinning.contains(block)) {
				event.getPlayer().sendMessage("This spinner is already spinning! Wait for it to finish first.");
				return;
			} event.getPlayer().sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "The roulette is spinning!");
			final Random rand = new Random();
			spinning.add(block);
			for(int i = 0; i < 30; ++i) {
				Bukkit.getScheduler().scheduleSyncDelayedTask(FancyRoulette.instance, new Runnable() {
					public void run() {
						Wool woolColor = new Wool(block.getType(), block.getData());
						int color = rand.nextInt(16);
						woolColor.setColor(DyeColor.values()[color]);
						event.getClickedBlock().setType(Material.WOOL);
						event.getClickedBlock().setData(woolColor.getData());
					}
				}, (long)(5 * i));
			}
			
			Bukkit.getScheduler().scheduleSyncDelayedTask(FancyRoulette.instance, new Runnable() {
				public void run() {
					Wool winningColor = new Wool(block.getType(), block.getData());
					onSpinEnd(winningColor, block);
					spinning.remove(block);
				}
			}, (long)(160));
		}
	}
	
	String getWoolNameIfWool(Block block) {
		if(block.getType().equals(Material.WOOL)) {
			Wool wool = new Wool(block.getType(), block.getData());
			if(wool.getColor().equals(DyeColor.BLACK))
				return (ChatColor.BLACK + "black" + ChatColor.WHITE);
			if(wool.getColor().equals(DyeColor.BLUE))
				return (ChatColor.DARK_BLUE + "blue" + ChatColor.WHITE);
			if(wool.getColor().equals(DyeColor.BROWN))
				return (ChatColor.GOLD + "brown" + ChatColor.WHITE);
			if(wool.getColor().equals(DyeColor.CYAN))
				return (ChatColor.AQUA + "cyan" + ChatColor.WHITE);
			if(wool.getColor().equals(DyeColor.GRAY))
				return (ChatColor.GRAY + "gray" + ChatColor.WHITE);
			if(wool.getColor().equals(DyeColor.GREEN))
				return (ChatColor.DARK_GREEN + "green" + ChatColor.WHITE);
			if(wool.getColor().equals(DyeColor.LIGHT_BLUE))
				return (ChatColor.BLUE + "light blue" + ChatColor.WHITE);
			if(wool.getColor().equals(DyeColor.LIME))
				return (ChatColor.GREEN + "lime" + ChatColor.WHITE);
			if(wool.getColor().equals(DyeColor.MAGENTA))
				return (ChatColor.DARK_PURPLE + "magenta" + ChatColor.WHITE);
			if(wool.getColor().equals(DyeColor.ORANGE))
				return (ChatColor.GOLD + "orange" + ChatColor.WHITE);
			if(wool.getColor().equals(DyeColor.PINK))
				return (ChatColor.LIGHT_PURPLE + "pink" + ChatColor.WHITE);
			if(wool.getColor().equals(DyeColor.PURPLE))
				return (ChatColor.DARK_PURPLE + "purple" + ChatColor.WHITE);
			if(wool.getColor().equals(DyeColor.RED))
				return (ChatColor.DARK_RED + "red" + ChatColor.WHITE);
			if(wool.getColor().equals(DyeColor.SILVER))
				return (ChatColor.GRAY + "silver" + ChatColor.WHITE);
			if(wool.getColor().equals(DyeColor.WHITE))
				return (ChatColor.WHITE + "white" + ChatColor.WHITE);
			if(wool.getColor().equals(DyeColor.YELLOW))
				return (ChatColor.YELLOW + "yellow" + ChatColor.WHITE);
		} return "some block";
	}
}
