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
		if (strings.length == 1) {
			Player player = Bukkit.getPlayer(strings[0]);
			
			if (player != null) {
				try {
					if (Objects.equals(InventorySyncer.getConfiguration().getString("dataStorage"), "mysql")) {
						InventoryWriter.writeECToDB(player);
					}
					else {
						InventoryWriter.writeECToFile(player);
					}
				}
				catch (IOException var7) {
					InventorySyncer.getInstance().getLogger().warning("Error writing player " + player.getUniqueId() + " (" + player.getName() + ") ender chest inventory");
					MessageSender.sendMessage(player, ChatColor.RED + "Error writing player " + player.getUniqueId() + " (" + player.getName() + ") ender chest inventory");
					var7.printStackTrace();
				}
				
				MessageSender.sendMessage(player, ChatColor.GREEN + "Successfully wrote player's ender chest inventory");
			} else {
				InventorySyncer.getInstance().getLogger().warning("Player '" + strings[0] + "' not found");
				MessageSender.sendMessage(commandSender, ChatColor.RED + "Player '" + strings[0] + "' not found");
			}
			
		}
		else {
			MessageSender.sendMessage(commandSender, ChatColor.RED + "Correct usage is /writeec <player>");
		}
		return true;
	}
	
}
