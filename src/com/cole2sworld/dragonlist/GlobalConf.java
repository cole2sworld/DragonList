package com.cole2sworld.dragonlist;

import java.util.Locale;

import org.bukkit.configuration.file.FileConfiguration;
/**
 * Manages the config
 *
 */
public final class GlobalConf {
	/**
	 * The configuration.
	 */
	public static FileConfiguration conf;
	/**
	 * The mode the whitelist is in
	 */
	public static WhitelistMode mode = WhitelistMode.NAME;
	/**
	 * Is DragonList enabled?
	 */
	public static boolean enabled = true;
	/**
	 * Message for when we kick a player
	 */
	public static String kickMessage;
	/**
	 * Load the configuration file.
	 */
	public static void loadConfig() {
		conf = Main.instance.getConfig();
		if (conf.get("type") == null) conf.set("type", "name");
		if (conf.get("enabled") == null) conf.set("enabled", true);
		if (conf.get("kickMessage") == null) conf.set("kickMessage", "The dragon overlord does not allow you onto this server!");
		mode = WhitelistMode.valueOf(conf.getString("type").toUpperCase(Locale.ENGLISH));
		enabled = conf.getBoolean("enabled");
		kickMessage = conf.getString("kickMessage");
		Main.instance.saveConfig();
	}
	/**
	 * Save the current fields to the config.yml
	 */
	public static void save() {
		conf.set("type", mode.toString());
		conf.set("enabled", enabled);
		conf.set("kickMessage", kickMessage);
		Main.instance.saveConfig();
	}
}
