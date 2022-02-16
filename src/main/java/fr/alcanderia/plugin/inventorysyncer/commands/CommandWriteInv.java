package fr.alcanderia.plugin.inventorysyncer.commands;

import fr.alcanderia.plugin.inventorysyncer.*;
import fr.alcanderia.plugin.inventorysyncer.services.*;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;

import java.io.*;
import java.util.*;

public class CommandWriteInv implements CommandExecutor {
	
	public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
		if (strings.length > 0) {
			Player player = null;
			
			try {
				player = Bukkit.getPlayer(strings[0]);
			} catch (NullPointerException var8) {
				InventorySyncer.getInstance().getLogger().warning("player not found");
				MessageSender.sendMessage(player, "Player not found");
			}
			
			assert player != null;
			
			try {
				if (Objects.equals(InventorySyncer.getConfiguration().getString("dataStorage"), "mysql")) {
					InventoryWriter.writeInvToDB(player);
				} else {
					InventoryWriter.writeInvToFile(player);
				}
			} catch (IOException var7) {
				InventorySyncer.getInstance().getLogger().warning("Error writing player " + player.getUniqueId() + " (" + player.getName() + ") inventory");
				MessageSender.sendMessage(player, ChatColor.RED + "Error writing player " + player.getUniqueId() + " (" + player.getName() + ") inventory");
				var7.printStackTrace();
			}
			
			MessageSender.sendMessage(player, "Successfully wrote player's inventory");
			return true;
		} else {
			MessageSender.sendMessage(commandSender, ChatColor.RED + "Correct usage is /writeinv <player>");
			return false;
		}
	}
}
