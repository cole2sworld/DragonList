package com.cole2sworld.dragonlist;

import java.util.List;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

public class AuthManager {
	/**
	 * De-authenticates this player (sets their metadata to false)
	 * @param player The player to deauth
	 */
	public static void deauth(Player player) {
		player.setMetadata("authenticated", new FixedMetadataValue(Main.instance, false));
	}
	/**
	 * Attempts authentication with this player. If the password is wrong, throws a IncorrectPasswordException.
	 * <br/><b>Note:</b><br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;The password should be plain-text, as this method will automatically hash the password when it is given.
	 * @param player The player to auth
	 * @param password The password to use<br/><br/>
	 * @throws IncorrectPasswordException If the given password is incorrect
	 * @throws PasswordNotSetException If the given player is not registered
	 */
	public static void auth(Player player, String password) throws IncorrectPasswordException, PasswordNotSetException {
		password = Util.computeHash(password);
		String correctPass = WhitelistManager.getHashedPassword(player.getName());
		if (correctPass == null) {
			throw new PasswordNotSetException();
		}
		if (correctPass.equals(password)) {
			player.setMetadata("authenticated", new FixedMetadataValue(Main.instance, true));
		}
		else {
			throw new IncorrectPasswordException();
		}
	}
	/**
	 * @return The player's authentication status
	 */
	public static boolean isAuthenticated(Player player) {
		List<MetadataValue> values = player.getMetadata("authenticated");
		for (MetadataValue value : values) {
			if (value.getOwningPlugin() == Main.instance) return value.asBoolean();
		}
		return false;
	}
	public static void changePassword(String name, String newPass) {
		if (WhitelistManager.pass.contains(name)) {
			WhitelistManager.pass.set(name, Util.computeHash(newPass));
		}
	}
}
