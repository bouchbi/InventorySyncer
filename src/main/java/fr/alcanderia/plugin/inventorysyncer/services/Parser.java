package fr.alcanderia.plugin.inventorysyncer.services;

import fr.alcanderia.plugin.inventorysyncer.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.util.io.*;
import org.yaml.snakeyaml.external.biz.base64Coder.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class Parser {
	private static      ConfigHandler config          = InventorySyncer.getConfiguration();
	public static final String        LabelXP         = "PlayerXP";
	public static final String        LabelLevel      = "PlayerLevel";
	public static final String        LabelHunger     = "PlayerHunger";
	public static final String        LabelSaturation = "PlayerSaturation";
	public static final String        LabelHealth     = "PlayerHealth";
	public static final String        LabelEffects    = "PlayerEffects";
	
	public static String Stringify(Player player) {
		StringBuilder inv = new StringBuilder();
		if (config.getBoolean("syncParameters.syncItems")) {
			for(int i = 0; i < player.getInventory().getSize(); ++i) {
				if (player.getInventory().getItem(i) != null && player.getInventory().getItem(i).getType() != Material.AIR) {
					String stack;
					try {
						ByteArrayOutputStream    outputStream = new ByteArrayOutputStream();
						BukkitObjectOutputStream dataOutput   = new BukkitObjectOutputStream(outputStream);
						dataOutput.writeObject(player.getInventory().getItem(i));
						stack = Base64Coder.encodeLines(outputStream.toByteArray());
						dataOutput.close();
					} catch (Exception var7) {
						throw new IllegalStateException("Unable to save item stacks", var7);
					}
					
					inv.append(i + "$" + stack + ";");
				}
			}
		}
		
		if (config.getBoolean("syncParameters.syncEffects")) {
			try {
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
				dataOutput.writeObject(player.getActivePotionEffects());
				inv.append("PlayerEffects$" + Base64Coder.encodeLines(outputStream.toByteArray()) + ";");
			} catch (Exception var6) {
				throw new IllegalStateException("Unable to save player effects", var6);
			}
		}
		
		if (config.getBoolean("syncParameters.syncXP")) {
			inv.append("PlayerXP$" + player.getExp() + ";");
		}
		
		if (config.getBoolean("syncParameters.syncXPLevel")) {
			inv.append("PlayerLevel$" + player.getLevel() + ";");
		}
		
		if (config.getBoolean("syncParameters.syncFoodLevel")) {
			inv.append("PlayerHunger$" + player.getFoodLevel() + ";");
		}
		
		if (config.getBoolean("syncParameters.syncSaturation")) {
			inv.append("PlayerSaturation$" + player.getSaturation() + ";");
		}
		
		if (config.getBoolean("syncParameters.syncHealth")) {
			inv.append("PlayerHealth$" + player.getHealth() + ";");
		}
		
		return inv.toString();
	}
	
	public static String readFile(UUID id) throws IOException {
		File file = new File(InventorySyncer.getInstance().getDataFolder(), id + ".txt");
		if (file.exists()) {
			BufferedReader reader = Files.newBufferedReader(Paths.get(file.toURI()));
			
			try {
				StringBuilder string = new StringBuilder();
				
				String line;
				while((line = reader.readLine()) != null) {
					string.append(line);
				}
				
				reader.close();
				String var5 = string.toString();
				return var5;
			} catch (IOException var15) {
				InventorySyncer.getInstance().getLogger().warning("Error reading player " + id + " inventory");
				var15.printStackTrace();
			} finally {
				try {
					reader.close();
				} catch (IOException var14) {
					InventorySyncer.getInstance().getLogger().warning("Cannot close BufferedReader, is it already closed ?");
					var14.printStackTrace();
				}
				
			}
			
			InventorySyncer.getInstance().getLogger().info("Inv of player " + id + " read successfully");
		}
		
		InventorySyncer.getInstance().getLogger().info("Inv of player " + id + " is not saved");
		return null;
	}
}
