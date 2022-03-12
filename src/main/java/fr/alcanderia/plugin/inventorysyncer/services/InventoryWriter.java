package fr.alcanderia.plugin.inventorysyncer.services;

import fr.alcanderia.plugin.inventorysyncer.*;
import fr.alcanderia.plugin.inventorysyncer.network.*;
import org.bukkit.entity.*;

import java.io.*;
import java.util.*;

public class InventoryWriter {
	public static void writeInvToFile(Player player) throws IOException {
		float  watchStart = (float)System.currentTimeMillis();
		String json       = Parser.StringifyInv(player);
		UUID   id         = player.getUniqueId();
		File   file       = new File(InventorySyncer.getInstance().getDataFolder(), id + ".txt");
		file.getParentFile().mkdir();
		file.createNewFile();
		Writer writer = new FileWriter(file, false);
		writer.write(json);
		writer.flush();
		writer.close();
		float watchStop = (float)System.currentTimeMillis();
		InventorySyncer.getInstance().getLogger().info("Inv of player " + id + " (" + player.getName() + ") saved successfully to file, took " + (watchStop - watchStart) + "ms");
	}
	
	public static void writeECToFile(Player player) throws IOException {
		float  watchStart = (float)System.currentTimeMillis();
		String json       = Parser.StringifyEC(player);
		UUID   id         = player.getUniqueId();
		File   file       = new File(InventorySyncer.getInstance().getDataFolder(), id + "_ender_chest.txt");
		file.getParentFile().mkdir();
		file.createNewFile();
		Writer writer = new FileWriter(file, false);
		writer.write(json);
		writer.flush();
		writer.close();
		float watchStop = (float)System.currentTimeMillis();
		InventorySyncer.getInstance().getLogger().info("EC of player " + id + " (" + player.getName() + ") saved successfully to file, took " + (watchStop - watchStart) + "ms");
	}
	
	public static void writeInvToDB(Player player) {
		float watchStart = (float)System.currentTimeMillis();
		String json = Parser.StringifyInv(player);
		UUID id = player.getUniqueId();
		String tabName = InventorySyncer.getConfiguration().getString("sqlCredentials.dbInvTableName");
		MySQLConnector.writeInv(id, MySQLConnector.getUserInv(id, tabName, false), tabName, true);
		MySQLConnector.writeInv(id, json, tabName, false);
		float watchStop = (float)System.currentTimeMillis();
		InventorySyncer.getInstance().getLogger().info("Inv of player " + id + " (" + player.getName() + ") saved successfully to database, took " + (watchStop - watchStart) + "ms");
	}
	
	public static void writeECToDB(Player player) {
		float watchStart = (float)System.currentTimeMillis();
		String json = Parser.StringifyEC(player);
		UUID id = player.getUniqueId();
		String tabName = InventorySyncer.getConfiguration().getString("sqlCredentials.dbInvTableName");
		MySQLConnector.writeInv(id, MySQLConnector.getUserInv(id, tabName, false), tabName, true);
		MySQLConnector.writeInv(id, json, tabName, false);
		float watchStop = (float)System.currentTimeMillis();
		InventorySyncer.getInstance().getLogger().info("EC of player " + id + " (" + player.getName() + ") saved successfully to database, took " + (watchStop - watchStart) + "ms");
	}
	
}
