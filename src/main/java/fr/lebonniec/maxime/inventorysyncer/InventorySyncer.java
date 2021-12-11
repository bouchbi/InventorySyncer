/*
 * Copyright (c) 2021.  All rights reserved.
 * Written by Maxime LE BONNIEC : https://github.com/Maxime-lbc
 */

package fr.lebonniec.maxime.inventorysyncer;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;


public class InventorySyncer extends JavaPlugin {
    private static FileConfiguration config;

    @Override
    public void onLoad()
    {
        saveDefaultConfig();
    }

    @Override
    public void onEnable()
    {
        getLogger().info("Plugin has started.");
        reloadConfig();
        config = this.getConfig();

    }

    @Override
    public void onDisable()
    {
        getLogger().info("Plugin has stopped.");

    }

    public static FileConfiguration getConfiguration()
    {
        return config;
    }

}
