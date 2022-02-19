package fr.alcanderia.plugin.inventorysyncer;

import fr.alcanderia.plugin.inventorysyncer.commands.*;
import fr.alcanderia.plugin.inventorysyncer.event.*;
import fr.alcanderia.plugin.inventorysyncer.network.*;
import fr.alcanderia.plugin.inventorysyncer.services.*;
import org.bukkit.*;
import org.bukkit.plugin.java.*;

import java.io.*;
import java.util.*;

public class InventorySyncer extends JavaPlugin {
	
	private static InventorySyncer INSTANCE;
	private static ConfigHandler   config;
	
	public void onLoad() {
		this.saveDefaultConfig();
	}
	
	public void onEnable() {
		this.getServer().getPluginManager().registerEvents(new ListenPlayers(), this);
		config = new ConfigHandler(this);
		this.getCommand("inventorySyncer").setExecutor(new CommandAll());
		this.getCommand("inventorySyncer").setExecutor(new CommandAll());
		this.getCommand("reloadISConfig").setExecutor(new CommandReload());
		this.getCommand("syncInv").setExecutor(new CommandSyncInv());
		this.getCommand("syncInv").setTabCompleter(new SyncsCommands());
		this.getCommand("writeInv").setExecutor(new CommandWriteInv());
		this.getCommand("writeInv").setTabCompleter(new SyncsCommands());
		this.getCommand("syncEC").setExecutor(new CommandSyncEC());
		this.getCommand("syncEC").setTabCompleter(new SyncsCommands());
		this.getCommand("writeEC").setExecutor(new CommandWriteEC());
		this.getCommand("writeEC").setTabCompleter(new SyncsCommands());
		this.reloadConfig();
		INSTANCE = this;
		if (Objects.equals(config.getString("dataStorage"), "mysql")) {
			MySQLConnector.initConnexion();
		}
		
		this.getLogger().info("Plugin has started");
	}
	
	public void onDisable() {
		if (config.getBoolean("saveAllOnServerStop")) {
			if (Objects.equals(config.getString("dataStorage"), "mysql")) {
				this.getLogger().info("initializing inventory syncing for all players before stopping server");
				Bukkit.getOnlinePlayers().forEach(InventoryWriter::writeInvToDB);
			}
			else
				if (Objects.equals(config.getString("dataStorage"), "file")) {
					this.getLogger().info("initializing inventory syncing for all players before stopping server");
					Bukkit.getOnlinePlayers().forEach(player -> {
						try {
							InventoryWriter.writeInvToFile(player);
						}
						catch (IOException e) {
							this.getLogger().warning("failed to write inv of player " + player.getUniqueId() + " (" + player.getName() + ") to file");
							e.printStackTrace();
						}
					});
				}
		}
		
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