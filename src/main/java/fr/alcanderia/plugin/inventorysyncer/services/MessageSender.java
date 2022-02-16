package fr.alcanderia.plugin.inventorysyncer.services;

import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;

public class MessageSender {
	
	private static final String messagePrefix = ChatColor.GOLD + "[" + ChatColor.GRAY + "Inventory" + ChatColor.YELLOW + "Syncer" + ChatColor.GOLD + "]" + ChatColor.RESET + " ";
	
	public static void sendMessage(CommandSender sender, String msg) {
		sender.sendMessage(messagePrefix + msg);
	}
	
	public static void sendGenericError(CommandSender sender) {
		sender.sendMessage(messagePrefix + "There was an error processing this command, please check the console for further information");
	}
}
