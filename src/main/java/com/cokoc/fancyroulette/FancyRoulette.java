package cokoc.fancyroulette;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class FancyRoulette extends JavaPlugin {
	public static FancyRoulette instance;
	public TableManager tableManager;
	public Economy economy;

	public void onEnable() {
		instance = this;
		saveDefaultConfig();
		
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
		
		getCommand("roulette").setExecutor(new RouletteCommandExecutor());
	}
	
	public void onDisable() {
		tableManager.saveData();
	}
}
