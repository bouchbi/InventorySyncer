package fr.alcanderia.plugin.inventorysyncer.commands;

import fr.alcanderia.plugin.inventorysyncer.*;
import fr.alcanderia.plugin.inventorysyncer.services.*;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;

import java.util.*;

public class CommandSyncInv implements CommandExecutor {
	
	public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
		if (strings.length > 0) {
			Player player = null;
			
			try {
				player = Bukkit.getPlayer(strings[0]);
			} catch (NullPointerException var7) {
				InventorySyncer.getInstance().getLogger().warning("player not found");
			}
			
			assert player != null;
			
			if (Objects.equals(InventorySyncer.getConfiguration().getString("dataStorage"), "mysql") ? InventoryReader.readInvAndApply(player, true) : InventoryReader.readInvAndApply(player, false)) {
				MessageSender.sendMessage(player, "Successfully sync player's inventory");
				return true;
			} else {
				MessageSender.sendMessage(player, "There was an error while attempting to sync inv, please check console for further information");
				return false;
			}
		} else {
			MessageSender.sendMessage(commandSender, ChatColor.RED + "Correct usage is /syncinv <player>");
			return false;
		}
	}
}