package fr.alcanderia.plugin.inventorysyncer.commands;

import fr.alcanderia.plugin.inventorysyncer.InventorySyncer;
import fr.alcanderia.plugin.inventorysyncer.services.InventoryReader;
import fr.alcanderia.plugin.inventorysyncer.services.MessageSender;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;

import java.util.*;

public class CommandSyncEC implements CommandExecutor {
	
	public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
		if (strings.length == 2 && strings[1].equals("backup") || strings.length == 2 && strings[1].equals("normal")) {
			Player player = Bukkit.getPlayer(strings[0]);
			boolean backup = strings[1].equals("backup");

			if (player != null) {
				if (Objects.equals(InventorySyncer.getConfiguration().getString("dataStorage"), "mysql") ? InventoryReader.readECInvAndApply(player, true, backup) : InventoryReader.readECInvAndApply(player, false, backup)) {
					MessageSender.sendMessage(player, ChatColor.GREEN + "Successfully synchronised player's ender chest inventory");
				}
				else {
					MessageSender.sendMessage(player, ChatColor.RED + "There was an error while attempting to sync ec, please check console for further information");
				}
			} else {
				InventorySyncer.getInstance().getLogger().warning("Player '" + strings[0] + "' not found");
				MessageSender.sendMessage(commandSender, ChatColor.RED + "Player '" + strings[0] + "' not found");
			}
		}
		else {
			MessageSender.sendMessage(commandSender, ChatColor.RED + "Correct usage is /syncec <player> <backup|normal>");
		}
		return true;
	}

}
