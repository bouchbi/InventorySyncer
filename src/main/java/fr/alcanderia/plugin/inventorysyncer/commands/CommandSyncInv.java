package fr.alcanderia.plugin.inventorysyncer.commands;

import fr.alcanderia.plugin.inventorysyncer.InventorySyncer;
import fr.alcanderia.plugin.inventorysyncer.services.InventoryReader;
import fr.alcanderia.plugin.inventorysyncer.services.MessageSender;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;

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