package fr.alcanderia.plugin.inventorysyncer.commands;

import fr.alcanderia.plugin.inventorysyncer.*;
import fr.alcanderia.plugin.inventorysyncer.services.*;
import org.bukkit.command.*;

public class CommandReload implements CommandExecutor {
	
	@Override public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
		if (args.length == 0) {
			InventorySyncer.getInstance().reloadConfig();
			MessageSender.sendMessage(sender, "Plugin reloaded");
			InventorySyncer.getInstance().getLogger().info("plugin reloaded");
			return true;
		}
		return false;
	}
	
}
