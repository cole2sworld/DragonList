package com.cole2sworld.dragonlist;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map.Entry;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

public class IPLogManager {
	/**
	 * The IP log configuration.
	 */
	public static YamlConfiguration ipLog = new YamlConfiguration();
	/**
	 * The IP log file.
	 */
	public static File logFile;
	/**
	 * Is the IP log initalized?
	 */
	public static boolean initalized = false;
	/**
	 * Initalize the IP log.
	 */
	public static void initalize() {
		if (initalized) return;
		File dataFolder = Main.instance.getDataFolder();
		dataFolder.mkdirs();
		logFile = new File(dataFolder.getPath()+"/iplog.yml");
		try {
			logFile.createNewFile();
		} catch (IOException e) {
			Main.instance.logger.severe("[DragonList] Error creating IP log file! ("+e.getMessage() == null ? e.getMessage() : ""+")");
		}
		ipLog = new YamlConfiguration();
		try {
			ipLog.load(logFile);
		} catch (FileNotFoundException e) {
			Main.instance.logger.severe("[DragonList] IP log file not found!");
		} catch (IOException e) {
			Main.instance.logger.severe("[DragonList] Error reading IP log file! ("+e.getMessage() == null ? e.getMessage() : ""+")");
		} catch (InvalidConfigurationException e) {
			Main.instance.logger.severe("[DragonList] IP log file is invalid!");
		}
		initalized = true;
	}
	/**
	 * Lookup a player's IP address.
	 * @param name The player name to look up.
	 * @return Null if the player has not been logged before, or an InetAddress if they have been logged.
	 */
	public static InetAddress lookupByName(String name) {
		try {
			return ipLog.get(name) == null ? null : InetAddress.getByName(ipLog.getString(name));
		} catch (UnknownHostException e) {
			ipLog.set(name, null);
			try {
				ipLog.save(logFile);
			} catch (IOException e1) {
				Main.instance.logger.severe("[DragonList] Error saving IP log file! ("+e.getMessage() == null ? "" : e.getMessage()+")");
			}
			return null;
		}
	}
	/**
	 * Look up a player's name.
	 * @param ip The IP to look up.
	 * @return Null if the player has not been logged before, or a String if they have been logged.
	 */
	public static String lookupByIP(InetAddress ip) {
		String newIP = ip.getHostAddress();
		for (Entry<String, Object> entry : ipLog.getValues(true).entrySet()) {
			if (entry.getValue().equals(newIP)) {
				return entry.getKey();
			}
		}
		return null;
	}
}
