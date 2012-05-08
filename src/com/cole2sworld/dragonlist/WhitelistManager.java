package com.cole2sworld.dragonlist;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

public class WhitelistManager {
	public static final String UNSET_MESSAGE = "DragonList - this password is not set";
	public static YamlConfiguration list;
	public static boolean initalized = false;
	public static File listFile;
	public static List<String> names;
	public static List<String> ips;
	public static ConfigurationSection pass;
	/**
	 * Initalize the manager. This gets the yaml file ready, and makes sure it's valid.
	 */
	public static void initalize() {
		if (initalized) return;
		File dataFolder = Main.instance.getDataFolder();
		dataFolder.mkdirs();
		listFile = new File(dataFolder.getPath()+"/whitelist.yml");
		try {
			listFile.createNewFile();
		} catch (IOException e) {
			Main.instance.logger.severe("[DragonList] Error creating list file! ("+e.getMessage() == null ? e.getMessage() : ""+")");
			return;
		}
		list = new YamlConfiguration();
		try {
			list.load(listFile);
		} catch (FileNotFoundException e) {
			Main.instance.logger.severe("[DragonList] Whitelist file not found!");
			return;
		} catch (IOException e) {
			Main.instance.logger.severe("[DragonList] Error reading whitelist file! ("+e.getMessage() == null ? e.getMessage() : ""+")");
			return;
		} catch (InvalidConfigurationException e) {
			Main.instance.logger.severe("[DragonList] Whitelist file is invalid!");
			return;
		}
		names = list.getStringList("name") == null ? list.getStringList("name") : new ArrayList<String>();
		ips = list.getStringList("ip") == null ? list.getStringList("ip") : new ArrayList<String>();
		pass = list.getConfigurationSection("pass") == null ? list.createSection("pass") : list.getConfigurationSection("pass");
		initalized = true;
	}
	/**
	 * Adds a player name to the name whitelist.
	 * @param name The player to add
	 */
	public static void addToNameWhitelist(String name) {
		System.out.println("[DragonList] "+name+" added to name whitelist.");
		names.add(name);
		list.set("name", names);
		save();
	}
	/**
	 * Adds a player ip to the ip whitelist.
	 * @param ip The IP to add
	 */
	public static void addToIPWhitelist(InetAddress ip) {
		System.out.println("[DragonList] "+ip.getHostAddress()+" added to ip whitelist.");
		ips.add(ip.getHostAddress());
		list.set("ip", ips);
		save();
	}
	/**
	 * Adds a name with password to the passworded whitelist, and hashes the password.
	 * @param name The name to add
	 * @param password The password to add
	 */
	public static void addToPasswordedWhitelist(String name, String password) {
		System.out.println("[DragonList] "+name+" added to passworded whitelist.");
		password = Util.computeHash(password);
		pass.set(name, password);
		save();
	}
	/**
	 * Adds a name with password to the passworded whitelist, and hashes the password.
	 * @param name The name to add
	 * @param password The password to add
	 */
	public static void addToPasswordedWhitelist(String name) {
		System.out.println("[DragonList] "+name+" added to passworded whitelist without password (will be prompted for password next login)");
		pass.set(name, UNSET_MESSAGE);
		save();
	}
	/**
	 * Removes a player from the name whitelist.
	 * @param name The name to remove
	 */
	public static void removeFromNameWhitelist(String name) {
		System.out.println("[DragonList] "+name+" removed from name whitelist.");
		names.remove(name);
		list.set("name", names);
		save();
	}
	/**
	 * Removes a ip from the ip whitelist.
	 * @param ip The IP to remove
	 */
	public static void removeFromIPWhitelist(InetAddress ip) {
		System.out.println("[DragonList] "+ip.getHostAddress()+" removed from ip whitelist.");
		ips.remove(ip.getHostAddress());
		list.set("ip", ips);
		save();
	}
	/**
	 * Removes a player from the passworded whitelist
	 * @param name The player to remove
	 */
	public static void removeFromPasswordWhitelist(String name) {
		System.out.println("[DragonList] "+name+" removed from passworded whitelist.");
		pass.set(name, null);
		save();
	}
	/**
	 * Import the vanilla whitelist (if enabled)
	 */
	public static void importVanillaWhitelist() {
		Server server = Bukkit.getServer();
		if (server.hasWhitelist()) {
			System.out.println("[DragonList] Vanilla whitelist enabled, importing...");
			Set<OfflinePlayer> whitelist = server.getWhitelistedPlayers();
			for (OfflinePlayer player : whitelist) {
				WhitelistManager.addToNameWhitelist(player.getName());
			}
			server.setWhitelist(false);
			System.out.println("[DragonList] Imported. Vanilla whitelist disabled.");
			save();
		}
	}
	/**
	 * Save the whitelist
	 */
	public static void save() {
		try {
			list.save(listFile);
		} catch (IOException e) {
			Main.instance.logger.severe("[DragonList] Failed to save whitelist ("+e.getMessage() == null ? e.getMessage() : ""+")");
		}
	}
	/**
	 * @param name Name to check, can also be an IP (in IP mode, names will be looked up, in name mode, IPs will be looked up)
	 * @return If the player is whitelisted, after proper lookups
	 */
	public static boolean isWhitelisted(String name) {
		if (GlobalConf.mode == WhitelistMode.IP) {
			name = IPLogManager.lookupByName(name).getHostAddress();
		}
		if (GlobalConf.mode == WhitelistMode.NAME) {
			return names.contains(name);
		} else if (GlobalConf.mode == WhitelistMode.PASSWORD) {
			return pass.contains(name) && AuthManager.isAuthenticated(Bukkit.getPlayer(name));
		} else {
			return ips.contains(name);
		}
	}
	/**
	 * @return The hashed password for the given username, or null if not registered.
	 */
	public static String getHashedPassword(String name) {
		return pass.getString(name);
	}
}
