package com.cole2sworld.dragonlist;

import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
/**
 * Manages restrictions.
 *
 */
public class RestrictionManager {
	static HashMap<Player, Location> frozen = new HashMap<Player, Location>();
	public static final Location FREEZE_LOC = new Location(Bukkit.getWorlds().get(0), 0, 4000, 0);
	public static void freeze(Player player) {
		if (isFrozen(player)) return;
		frozen.put(player, player.getLocation());
		player.teleport(FREEZE_LOC);
	}
	public static void thaw(Player player) {
		if (!isFrozen(player)) return;
		player.teleport(frozen.get(player));
		frozen.remove(player);
	}
	public static boolean isFrozen(Player player) {
		return frozen.containsKey(player);
	}
}
