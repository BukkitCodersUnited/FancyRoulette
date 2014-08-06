package com.cokoc.fancyroulette;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.bukkit.block.Block;

public class TableManager extends PersistentManager {
	private HashMap<Integer, ArrayList<SerializableLocation>> tilesLocations;
	private HashMap<Integer, SerializableLocation> spinnersLocations;
	
	public TableManager() {
		tilesLocations = new HashMap<Integer, ArrayList<SerializableLocation>>();
		spinnersLocations = new HashMap<Integer, SerializableLocation>();
	}
	
	public void addBlockToTable(int tableId, Block block) {
		if(tilesLocations.containsKey(tableId)) {
			SerializableLocation blockLocation = new SerializableLocation(block.getLocation());
			tilesLocations.get(tableId).add(blockLocation);
		} else {
			ArrayList<SerializableLocation> locations = new ArrayList<SerializableLocation>();
			locations.add(new SerializableLocation(block.getLocation()));
			tilesLocations.put(tableId, locations);
		}
	}
	
	public void setTableSpinner(int tableId, Block block) {
		spinnersLocations.put(tableId, new SerializableLocation(block.getLocation()));
	}
	
	public SerializableLocation getTableSpinner(int tableId) {
		return spinnersLocations.get(tableId);
	}
	
	public boolean removeBlockFromRoulette(Block block) {
		Set<Integer> tilesKeys = tilesLocations.keySet();
		for(Integer key : tilesKeys) {
			SerializableLocation blockLocation = new SerializableLocation(block.getLocation());
			if(tilesLocations.get(key).contains(blockLocation)) {
				tilesLocations.get(key).remove(blockLocation);
				return true;
			}
		} Set<Integer> spinnersKeys = spinnersLocations.keySet();
		for(Integer key : spinnersKeys) {
			SerializableLocation blockLocation = new SerializableLocation(block.getLocation());
			if(spinnersLocations.get(key).equals(blockLocation)) {
				spinnersLocations.remove(key);
				return true;
			}
		} return false;
	}
	
	public boolean isBlockAlreadyInRoulette(Block block) {
		Set<Integer> tilesKeys = tilesLocations.keySet();
		for(Integer key : tilesKeys) {
			SerializableLocation blockLocation = new SerializableLocation(block.getLocation());
			if(tilesLocations.get(key).contains(blockLocation))
				return true;
		} Set<Integer> spinnersKeys = spinnersLocations.keySet();
		for(Integer key : spinnersKeys) {
			SerializableLocation blockLocation = new SerializableLocation(block.getLocation());
			if(spinnersLocations.get(key).equals(blockLocation))
				return true;
		} return false;
	}
	
	public boolean isBlockTile(Block block) {
		Set<Integer> tilesKeys = tilesLocations.keySet();
		SerializableLocation blockLocation = new SerializableLocation(block.getLocation());
		for(Integer key : tilesKeys) {
			for(int i = 0; i < tilesLocations.get(key).size(); ++i) {
				SerializableLocation keyBlockLocation = tilesLocations.get(key).get(i);
				if(blockLocation.equals(keyBlockLocation))
					return true;
			}
		} return false;
	}
	
	public boolean isBlockSpinner(Block block) {
		Set<Integer> spinnersKeys = spinnersLocations.keySet();
		for(Integer key : spinnersKeys) {
			SerializableLocation blockLocation = new SerializableLocation(block.getLocation());
			if(spinnersLocations.get(key).equals(blockLocation))
				return true;
		} return false;
	}
	
	public int getTableId(Block block) {
		Set<Integer> tilesKeys = tilesLocations.keySet();
		for(Integer key : tilesKeys) {
			SerializableLocation blockLocation = new SerializableLocation(block.getLocation());
			if(tilesLocations.get(key).contains(blockLocation))
				return key;
		} Set<Integer> spinnersKeys = spinnersLocations.keySet();
		for(Integer key : spinnersKeys) {
			SerializableLocation blockLocation = new SerializableLocation(block.getLocation());
			if(spinnersLocations.get(key).equals(blockLocation))
				return key;;
		} return -1;
	}
	
	public ArrayList<SerializableLocation> getTableTiles(int tableId) {
		return tilesLocations.get(tableId);
	}
	
	@SuppressWarnings("unchecked")
	public void loadData() {
		try {
			tilesLocations = (HashMap<Integer, ArrayList<SerializableLocation>>) load("plugins/FancyRoulette/tileslocations.bin");
			spinnersLocations = (HashMap<Integer, SerializableLocation>) load("plugins/FancyRoulette/spinnerslocations.bin");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void saveData() {
		try {
			save(tilesLocations, "plugins/FancyRoulette/tileslocations.bin");
			save(spinnersLocations, "plugins/FancyRoulette/spinnerslocations.bin");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
