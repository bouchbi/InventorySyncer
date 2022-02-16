package fr.alcanderia.plugin.inventorysyncer.commands;

import fr.alcanderia.plugin.inventorysyncer.*;
import fr.alcanderia.plugin.inventorysyncer.services.*;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;

import java.util.*;

public class CommandSyncEC implements CommandExecutor {
	
	public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
		if (strings.length == 1) {
			Player player = null;
			
			try {
				player = Bukkit.getPlayer(strings[0]);
			} catch (NullPointerException var7) {
				InventorySyncer.getInstance().getLogger().warning("player not found");
			}
			
			assert player != null;
			
			if (Objects.equals(InventorySyncer.getConfiguration().getString("dataStorage"), "mysql") ? InventoryReader.readECInvAndApply(player, true) : InventoryReader.readECInvAndApply(player, false)) {
				MessageSender.sendMessage(player, "Successfully sync player's ender chest");
				return true;
			} else {
				MessageSender.sendMessage(player, "There was an error while attempting to sync ec, please check console for further information");
				return false;
			}
		} else {
			MessageSender.sendMessage(commandSender, ChatColor.RED + "Correct usage is /syncec <player>");
			return false;
		}
	}
}
