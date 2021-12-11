package fr.lebonniec.maxime.inventorysyncer.services;

import com.sun.istack.internal.*;
import org.bukkit.*;
import org.bukkit.entity.*;

import java.util.*;

public class Deserializer {
	
	public static void Deserialize(String str, @NotNull UUID uuid) {
		Player player  = Bukkit.getPlayer(uuid);
		String[] slots = str.split("/")[0].split(";");
		
		for (String s : slots) {
		
		}
	}
}
