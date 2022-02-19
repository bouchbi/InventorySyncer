package fr.alcanderia.plugin.inventorysyncer.commands;

import fr.alcanderia.plugin.inventorysyncer.InventorySyncer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CommandAll implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        switch (args[0]) {
            case "reload":
                return new CommandReload().onCommand(sender, command, label, args);
            case "syncec":
                return new CommandSyncEC().onCommand(sender, command, label, Arrays.copyOfRange(args, 1, args.length));
            case "syncinv":
                return new CommandSyncInv().onCommand(sender, command, label, Arrays.copyOfRange(args, 1, args.length));
            case "writeec":
                return new CommandWriteEC().onCommand(sender, command, label, Arrays.copyOfRange(args, 1, args.length));
            case "writeinv":
                return new CommandWriteInv().onCommand(sender, command, label, Arrays.copyOfRange(args, 1, args.length));
            default:
                return false;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        final List<String> commands = new ArrayList<>();
        final List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            commands.add("reload");
            commands.add("syncec");
            commands.add("syncinv");
            commands.add("writeec");
            commands.add("writeinv");
            StringUtil.copyPartialMatches(args[0], commands, completions);
        } else if (args.length == 2) {
            Bukkit.getOnlinePlayers().forEach(pl -> commands.add(pl.getName()));
            StringUtil.copyPartialMatches(args[1], commands, completions);
        }

        Collections.sort(completions);
        return completions;
    }
}
