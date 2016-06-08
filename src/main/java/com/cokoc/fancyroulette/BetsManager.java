package cokoc.fancyroulette;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class BetsManager {
	private HashMap<Player, ArrayList<Block>> bets = new HashMap<Player, ArrayList<Block>>();
	
	public void addBet(Player player, Block bet) {
		if(bets.containsKey(player))
			bets.get(player).add(bet);
		else {
			ArrayList<Block> playerBets = new ArrayList<Block>();
			bets.put(player, playerBets);
		}
	}
	
	public void removeBetForPlayer(Player player, Block bet) {
		if(! bets.containsKey(player))
			return;
		bets.get(player).remove(bet);
	}
	
	public void removeBet(Block bet) {
		Set<Player> betsKeys = bets.keySet();
		for(Player key : betsKeys) {
			if(bets.get(key).contains(bet))
				bets.get(key).remove(bet);
		}
	}
	
	public boolean hasBettedOnBlock(Player player, Block bet) {
		if(! bets.containsKey(player))
			return false;
		if(bets.get(player).contains(bet))
			return true;
		return false;
	}
	
	public boolean isBlockBettedOn(Block block) {
		Set<Player> betsKeys = bets.keySet();
		for(Player key : betsKeys) {
			if(bets.get(key).contains(block))
				return true;
		} return false;
	}
	
	public Player whoBettedOn(Block block) {
		Set<Player> betsKeys = bets.keySet();
		for(Player key : betsKeys) {
			if(bets.get(key).contains(block))
				return key;
		} return null;
	}
	
	public int getNumberOfBets(Player player) {
		if(!bets.containsKey(player))
			return 0;
		return bets.get(player).size();
	}
}
