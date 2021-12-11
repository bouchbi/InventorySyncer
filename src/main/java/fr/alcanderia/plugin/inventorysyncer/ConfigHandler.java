package fr.alcanderia.plugin.inventorysyncer;

import java.io.*;

public class ConfigHandler {
	private InventorySyncer plugin;
	
	public ConfigHandler(InventorySyncer plugin) {
		this.plugin = plugin;
		this.loadConfig();
	}
	
	public void loadConfig() {
		File folder = new File(this.plugin.getDataFolder().getAbsolutePath());
		if (!folder.exists()) {
			folder.mkdir();
		}
		
		File configFile = new File(this.plugin.getDataFolder() + System.getProperty("file.separator") + "config.yml");
		if (!configFile.exists()) {
			this.plugin.getLogger().info("No config file, plugin will create one");
			this.plugin.saveDefaultConfig();
		}
		
		try {
			this.plugin.getLogger().info("Loading config file in plugin directory");
			this.plugin.getConfig().load(configFile);
		} catch (Exception var4) {
			this.plugin.getLogger().warning("Unable to load config file, please try recreating one !");
			var4.printStackTrace();
		}
		
	}
	
	public String getString(String key) {
		if (!this.plugin.getConfig().contains(key)) {
			this.plugin.getLogger().warning("Could not find " + key + " in config file, please check if it is correctly written (Or recreate one by deleting it)");
			return "notfoundKey: " + key;
		} else {
			return this.plugin.getConfig().getString(key);
		}
	}
	
	public boolean getBoolean(String key) {
		if (!this.plugin.getConfig().contains(key)) {
			this.plugin.getLogger().warning("Could not find " + key + " in config file, please check if it is correctly written (Or recreate one by deleting it)");
			return false;
		} else {
			return this.plugin.getConfig().getBoolean(key);
		}
	}
}
