package fr.alcanderia.plugin.inventorysyncer.commands;

import fr.alcanderia.plugin.inventorysyncer.*;
import fr.alcanderia.plugin.inventorysyncer.services.*;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;

import java.util.*;

public class CommandSyncInv implements CommandExecutor {
	
	public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
		if (strings.length == 1) {
			Player player = Bukkit.getPlayer(strings[0]);
			
			if (player != null) {
				if (Objects.equals(InventorySyncer.getConfiguration().getString("dataStorage"), "mysql") ? InventoryReader.readInvAndApply(player, true) : InventoryReader.readInvAndApply(player, false)) {
					MessageSender.sendMessage(player, ChatColor.GREEN + "Successfully synchronised player's inventory");
				}
				else {
					MessageSender.sendMessage(player, ChatColor.RED + "There was an error while attempting to sync inv, please check console for further information");
				}
			} else {
				InventorySyncer.getInstance().getLogger().warning("Player '" + strings[0] + "' not found");
				MessageSender.sendMessage(commandSender, ChatColor.RED + "Player '" + strings[0] + "' not found");
			}
		}
		else {
			MessageSender.sendMessage(commandSender, ChatColor.RED + "Correct usage is /syncinv <player>");
		}
		return true;
	}
	
}