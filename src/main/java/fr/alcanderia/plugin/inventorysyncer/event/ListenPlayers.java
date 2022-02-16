package fr.alcanderia.plugin.inventorysyncer.event;

import fr.alcanderia.plugin.inventorysyncer.*;
import fr.alcanderia.plugin.inventorysyncer.services.*;
import org.bukkit.event.*;
import org.bukkit.event.player.*;

import java.io.*;
import java.util.*;

public class ListenPlayers implements Listener {
	
	@EventHandler
	public static void onPlayerQuit(PlayerQuitEvent e) throws IOException {
		if (InventorySyncer.getConfiguration().getBoolean("syncOnJoinAndLeave.inv")) {
			if (Objects.equals(InventorySyncer.getConfiguration().getString("dataStorage"), "mysql")) {
				InventoryWriter.writeInvToDB(e.getPlayer());
			}
			else {
				InventoryWriter.writeInvToFile(e.getPlayer());
			}
		}
		if (InventorySyncer.getConfiguration().getBoolean("syncOnJoinAndLeave.ec")) {
			if (Objects.equals(InventorySyncer.getConfiguration().getString("dataStorage"), "mysql")) {
				InventoryWriter.writeECToDB(e.getPlayer());
			}
			else {
				InventoryWriter.writeECToFile(e.getPlayer());
			}
		}
	}
	
	@EventHandler
	public static void onPlayerJoin(PlayerJoinEvent e) {
		if (InventorySyncer.getConfiguration().getBoolean("syncOnJoinAndLeave.ec"))
			InventoryReader.readECInvAndApply(e.getPlayer(), Objects.equals(InventorySyncer.getConfiguration().getString("dataStorage"), "mysql"));
		
		if (InventorySyncer.getConfiguration().getBoolean("syncOnJoinAndLeave.inv")) {
			if (!InventorySyncer.getConfiguration().getBoolean("enablePickUpOnSync"))
				e.getPlayer().setCanPickupItems(false);
			
			InventoryReader.readInvAndApply(e.getPlayer(), Objects.equals(InventorySyncer.getConfiguration().getString("dataStorage"), "mysql"));
			if (!InventorySyncer.getConfiguration().getBoolean("enablePickUpOnSync"))
				e.getPlayer().setCanPickupItems(true);
		}
		
	}
	
}