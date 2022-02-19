package fr.alcanderia.plugin.inventorysyncer.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SyncsCommands implements TabCompleter {
    public static final int requiredArgs = 1;

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        final List<String> completions = new ArrayList<>();
        final List<String> players = new ArrayList<>();
        Bukkit.getOnlinePlayers().forEach(pl -> players.add(pl.getName()));
        StringUtil.copyPartialMatches(args[0], players, completions);
        Collections.sort(completions);
        return completions;
    }
}
