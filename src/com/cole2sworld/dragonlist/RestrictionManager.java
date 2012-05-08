package com.cole2sworld.dragonlist;

import java.util.List;
import java.util.Vector;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

public class RestrictionManager {
	static Vector<Player> frozen = new Vector<Player>();
	public static final Location FREEZE_LOC = new Location(Bukkit.getWorlds().get(0), 0, 4000, 0);
	public static void freeze(Player player) {
		player.setMetadata("oldpos", new FixedMetadataValue(Main.instance, player.getLocation()));
		player.teleport(FREEZE_LOC);
		frozen.add(player);
	}
	public static void thaw(Player player) {
		if (!player.hasMetadata("oldpos")) return;
		List<MetadataValue> values = player.getMetadata("oldpos");
		for (MetadataValue value : values) {
			if (value.getOwningPlugin() == Main.instance) player.teleport((Location)value.value());
		}
		frozen.remove(player);
	}
	public static boolean isFrozen(Player player) {
		return frozen.contains(player);
	}
}
