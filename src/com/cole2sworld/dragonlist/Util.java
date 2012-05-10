package com.cole2sworld.dragonlist;

import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
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
			System.out.println("[DragonList] SHA-1 encryption not available! Password mode will not work correctly!");
			return "";
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
}
