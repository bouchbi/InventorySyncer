package fr.alcanderia.plugin.inventorysyncer.commands;

import fr.alcanderia.plugin.inventorysyncer.*;
import fr.alcanderia.plugin.inventorysyncer.services.*;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;

import java.io.*;
import java.util.*;

public class CommandWriteEC implements CommandExecutor {
	
	public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
		if (strings.length > 0) {
			Player player = null;
			
			try {
				player = Bukkit.getPlayer(strings[0]);
			} catch (NullPointerException var8) {
				InventorySyncer.getInstance().getLogger().warning("player not found");
			}
			
			assert player != null;
			
			try {
				if (Objects.equals(InventorySyncer.getConfiguration().getString("dataStorage"), "mysql")) {
					InventoryWriter.writeECToDB(player);
				} else {
					InventoryWriter.writeECToFile(player);
				}
			} catch (IOException var7) {
				InventorySyncer.getInstance().getLogger().warning("Error writing player " + player.getUniqueId() + " (" + player.getName() + ") inv");
				var7.printStackTrace();
			}
			
			return true;
		} else {
			return false;
		}
	}
}
