package com.cole2sworld.dragonlist;

import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
/**
 * Generic utilities
 *
 */
public final class Util {
	public static String computeHash(String str) {
		MessageDigest digest = null;
		try {
			digest = MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException e) {
			System.out.println("[DragonList] SHA-1 encryption not available! Password mode will be insecure!");
			return str;
		}
		digest.reset();
		digest.update(str.getBytes());
		byte[] digested = digest.digest();
		StringBuffer builder = new StringBuffer(digested.length * 2);
		for (int i = 0; i < digested.length; i++){
			int v = digested[i] & 0xff;
			if (v < 16) {
				builder.append('0');
			}
			builder.append(Integer.toHexString(v));
		}
		return builder.toString().toUpperCase(Locale.ENGLISH);
	}
	public static byte[] processIp(String str) throws UnknownHostException {
		String[] ip = str.split("\\.");
		byte[] procIp = new byte[4];
		if (ip.length < 4) {
			throw new UnknownHostException();
		}
		procIp[0] = Byte.parseByte(ip[0]);
		procIp[1] = Byte.parseByte(ip[1]);
		procIp[2] = Byte.parseByte(ip[2]);
		procIp[3] = Byte.parseByte(ip[3]);
		return procIp;
	}
	public static String stringizeLocation(Location loc) {
		return loc.getX()+":"+loc.getY()+":"+loc.getZ()+":"+loc.getPitch()+":"+loc.getYaw()+":"+loc.getWorld().getUID().toString();
	}
	public static Location locationizeString(String val) {
		String[] split = val.split(":");
		try {
			return new Location(Bukkit.getWorld(UUID.fromString(split[5])), Double.parseDouble(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2]), Float.parseFloat(split[4]), Float.parseFloat(split[3]));
		} catch (NumberFormatException e) {
			return Bukkit.getWorlds().get(0).getSpawnLocation();
		}
	}
}
