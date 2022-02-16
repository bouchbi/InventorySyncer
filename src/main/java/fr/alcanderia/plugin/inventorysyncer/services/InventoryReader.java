package fr.alcanderia.plugin.inventorysyncer.services;

import fr.alcanderia.plugin.inventorysyncer.*;
import fr.alcanderia.plugin.inventorysyncer.network.*;
import org.apache.commons.lang.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.*;
import org.bukkit.potion.*;
import org.bukkit.util.io.*;
import org.yaml.snakeyaml.external.biz.base64Coder.*;

import java.io.*;
import java.util.*;

public class InventoryReader {
	
	public static boolean readECInvAndApply(Player player, boolean toDB) {
		try {
			String inv = null;
			if (!toDB) {
				inv = Parser.readFile(player.getUniqueId(), true);
			} else {
				inv = MySQLConnector.getUserInv(player.getUniqueId(), InventorySyncer.getConfiguration().getString("sqlCredentials.dbECTableName"));
			}
			
			if (inv != null) {
				long watchStart = System.currentTimeMillis();
				player.getInventory().clear();
				inv = StringUtils.removeEnd(inv, ";");
				String[] slots = inv.split(";");
				int length = slots.length;
				
				for (int i = 0; i < length; i++) {
					String slot = slots[i];
					ItemStack stack;
					try {
						ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(slot.split("\\$")[1]));
						BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
						stack = (ItemStack)dataInput.readObject();
						dataInput.close();
					} catch (ClassNotFoundException var13) {
						throw new IOException("Unable to decode class type", var13);
					}
					
					player.getEnderChest().setItem(Integer.parseInt(slot.split("\\$")[0]), stack);
				}
				
				long watchEnd = System.currentTimeMillis();
				InventorySyncer.getInstance().getLogger().info("EC Inventory of player " + player.getUniqueId() + " (" + player.getName() + ") restored successfully, took " + (watchEnd - watchStart) + "ms");
			} else {
				InventorySyncer.getInstance().getLogger().warning("EC Inventory of player " + player.getUniqueId() + " (" + player.getName() + ") is null");
			}
			
			return true;
		} catch (IOException var15) {
			InventorySyncer.getInstance().getLogger().warning("Error reading player " + player.getUniqueId() + " ec inv");
			var15.printStackTrace();
			return false;
		}
	}
	
	public static boolean readInvAndApply(Player player, boolean toDB) {
		try {
			String inv = null;
			if (!toDB) {
				inv = Parser.readFile(player.getUniqueId(), false);
			} else {
				inv = MySQLConnector.getUserInv(player.getUniqueId(), InventorySyncer.getConfiguration().getString("sqlCredentials.dbInvTableName"));
			}
			
			if (inv != null) {
				long watchStart = System.currentTimeMillis();
				player.getInventory().clear();
				inv = StringUtils.removeEnd(inv, ";");
				String[] slots = inv.split(";");
				String[] var6 = slots;
				int var7 = slots.length;
				
				for(int var8 = 0; var8 < var7; ++var8) {
					String slot = var6[var8];
					if (slot.contains("PlayerXP")) {
						player.setExp(Float.parseFloat(slot.split("\\$")[1]));
					} else if (slot.contains("PlayerLevel")) {
						player.setLevel(Integer.parseInt(slot.split("\\$")[1]));
					} else if (slot.contains("PlayerHunger")) {
						player.setFoodLevel(Integer.parseInt(slot.split("\\$")[1]));
					} else if (slot.contains("PlayerSaturation")) {
						player.setSaturation(Float.parseFloat(slot.split("\\$")[1]));
					} else if (slot.contains("PlayerHealth")) {
						player.setHealth(Double.parseDouble(slot.split("\\$")[1]));
					} else if (slot.contains("PlayerEffects")) {
						try {
							ByteArrayInputStream     inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(slot.split("\\$")[1]));
							BukkitObjectInputStream  dataInput = new BukkitObjectInputStream(inputStream);
							Collection<PotionEffect> effects   = (Collection)dataInput.readObject();
							player.addPotionEffects(effects);
						} catch (ClassNotFoundException var14) {
							throw new IOException("Unable to decode class type", var14);
						}
					} else {
						ItemStack stack;
						try {
							ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(slot.split("\\$")[1]));
							BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
							stack = (ItemStack)dataInput.readObject();
							dataInput.close();
						} catch (ClassNotFoundException var13) {
							throw new IOException("Unable to decode class type", var13);
						}
						
						player.getInventory().setItem(Integer.parseInt(slot.split("\\$")[0]), stack);
					}
				}
				
				long watchEnd = System.currentTimeMillis();
				InventorySyncer.getInstance().getLogger().info("Inventory of player " + player.getUniqueId() + " (" + player.getName() + ") restored successfully, took " + (watchEnd - watchStart) + "ms");
			} else {
				InventorySyncer.getInstance().getLogger().warning("Inventory of player " + player.getUniqueId() + " (" + player.getName() + ") is null");
			}
			
			return true;
		} catch (IOException var15) {
			InventorySyncer.getInstance().getLogger().warning("Error reading player " + player.getUniqueId() + " inv");
			var15.printStackTrace();
			return false;
		}
	}
}
