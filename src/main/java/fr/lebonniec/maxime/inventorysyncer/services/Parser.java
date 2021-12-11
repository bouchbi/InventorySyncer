/*
 * Copyright (c) 2021.  All rights reserved.
 * Written by Maxime LE BONNIEC : https://github.com/Maxime-lbc
 */

package fr.lebonniec.maxime.inventorysyncer.services;

import org.bukkit.entity.*;

public class Parser {

    public static String Stringify (Player player, boolean syncArmor) {
        StringBuilder s = new StringBuilder();
        
        for (int i = 0; i < player.getInventory().getSize() - 4; i++)
            s.append(i).append(":").append(player.getInventory().getItem(i)).append(";");
        
        if (syncArmor) {
            s.append("/head:").append(player.getInventory().getHelmet()).append(";");
            s.append("chestplate:").append(player.getInventory().getChestplate()).append(";");
            s.append("legs:").append(player.getInventory().getLeggings()).append(";");
            s.append("boots:").append(player.getInventory().getBoots()).append(";");
        }
        
        return s.toString();
    }
}
