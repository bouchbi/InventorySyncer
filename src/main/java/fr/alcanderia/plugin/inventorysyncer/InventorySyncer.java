package fr.alcanderia.plugin.inventorysyncer;

import fr.alcanderia.plugin.inventorysyncer.commands.*;
import fr.alcanderia.plugin.inventorysyncer.event.*;
import fr.alcanderia.plugin.inventorysyncer.network.*;
import org.bukkit.plugin.java.*;

import java.util.*;

public class InventorySyncer extends JavaPlugin {
	private static InventorySyncer INSTANCE;
	private static ConfigHandler config;
	
	public void onLoad() {
		this.saveDefaultConfig();
	}
	
	public void onEnable() {
		this.getServer().getPluginManager().registerEvents(new ListenPlayers(), this);
		config = new ConfigHandler(this);
		this.getCommand("syncInv").setExecutor(new CommandSyncInv());
		this.getCommand("writeInv").setExecutor(new CommandWriteInv());
		this.reloadConfig();
		INSTANCE = this;
		if (Objects.equals(config.getString("dataStorage"), "mysql")) {
			MySQLConnector.initConnexion();
		}
		
		this.getLogger().info("Plugin has started");
	}
	
	public void onDisable() {
		if (Objects.equals(config.getString("dataStorage"), "mysql")) {
			MySQLConnector.closeConnexion();
		}
		
		this.getLogger().info("Plugin has stopped");
	}
	
	public static InventorySyncer getInstance() {
		return INSTANCE;
	}
	
	public static ConfigHandler getConfiguration() {
		return config;
	}
}