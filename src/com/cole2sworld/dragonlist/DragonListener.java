package com.cole2sworld.dragonlist;

import java.io.IOException;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@SuppressWarnings("static-method")
/**
 * Listens for events we want
 *
 */
public final class DragonListener implements Listener {
	@EventHandler
	public void onBreak(final BlockBreakEvent event) {
		if (RestrictionManager.isFrozen(event.getPlayer())) {
			event.getPlayer().sendMessage(
					ChatColor.RED + "No, bad " + event.getPlayer().getName()
							+ "! You can't do that while frozen!");
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onChat(final PlayerChatEvent event) {
		if (RestrictionManager.isFrozen(event.getPlayer())) {
			event.setCancelled(true);
			if (event.getMessage().equalsIgnoreCase("u00a74u00a75u00a73u00a74v|1")) return;
			try {
				AuthManager.auth(event.getPlayer(), event.getMessage());
				RestrictionManager.thaw(event.getPlayer());
				event.getPlayer().sendMessage(ChatColor.GREEN + "Access granted!");
			} catch (final IncorrectPasswordException e) {
				RestrictionManager.thaw(event.getPlayer());
				event.getPlayer().kickPlayer(
						"Incorrect password! Make sure any chat prefixes are turned off!");
			} catch (final PasswordNotSetException e) {
				if (AuthManager.badPasswords.contains(event.getMessage())) {
					RestrictionManager.thaw(event.getPlayer());
					event.getPlayer().kickPlayer("Invalid password!");
					return;
				}
				event.getPlayer().sendMessage(
						ChatColor.GREEN + "Password set to " + event.getMessage());
				AuthManager.changePassword(event.getPlayer().getName(), event.getMessage());
				try {
					AuthManager.auth(event.getPlayer(), event.getMessage());
				} catch (final IncorrectPasswordException e1) {
					Main.LOG.warning("[DragonList] Something wierd happened, and the password we JUST set is incorrect.");
				} catch (final PasswordNotSetException e1) {
					Main.LOG.warning("[DragonList] Something wierd happened, and the password we JUST set is incorrect.");
				}
				RestrictionManager.thaw(event.getPlayer());
			}
		}
	}
	
	@EventHandler
	public void onCommand(final PlayerCommandPreprocessEvent event) {
		if (RestrictionManager.isFrozen(event.getPlayer())) {
			event.setCancelled(true);
			if (event.getMessage().equalsIgnoreCase("we cui")) return;
			event.getPlayer().sendMessage(
					ChatColor.RED + "No, bad " + event.getPlayer().getName()
							+ "! You can't use that while frozen!");
		}
	}
	
	@EventHandler
	public void onInteract(final PlayerInteractEvent event) {
		if (RestrictionManager.isFrozen(event.getPlayer())) {
			event.getPlayer().sendMessage(
					ChatColor.RED + "No, bad " + event.getPlayer().getName()
							+ "! You can't do that while frozen!");
			event.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onJoin(final PlayerJoinEvent event) {
		if (!GlobalConf.enabled) return;
		final Player player = event.getPlayer();
		AuthManager.deauth(player);
		if (WhitelistManager.pass.contains(player.getName())) {
			if (WhitelistManager.pass.getString(player.getName()).equals(
					WhitelistManager.UNSET_MESSAGE)) {
				RestrictionManager.freeze(player);
				player.sendMessage(ChatColor.BLUE
						+ "Please enter a password - this can be changed later through /whitelist setpass <password>");
			} else {
				RestrictionManager.freeze(player);
				player.sendMessage(ChatColor.BLUE + "Enter your password");
			}
		}
	}
	
	@EventHandler
	public void onKick(final PlayerKickEvent event) {
		RestrictionManager.thaw(event.getPlayer());
	}
	
	@EventHandler
	public void onLogin(final PlayerLoginEvent event) {
		if (!GlobalConf.enabled) return;
		if (!WhitelistManager.isWhitelisted(event.getPlayer().getName())) {
			event.disallow(Result.KICK_WHITELIST, GlobalConf.kickMessage);
			return;
		}
	}
	
	@EventHandler
	public void onMove(final PlayerMoveEvent event) {
		if (RestrictionManager.isFrozen(event.getPlayer())) {
			event.getPlayer().teleport(RestrictionManager.FREEZE_LOC);
			event.getPlayer().setFallDistance(0);
			event.getPlayer().setFireTicks(0);
		}
	}
	
	@EventHandler
	public void onPlace(final BlockPlaceEvent event) {
		if (RestrictionManager.isFrozen(event.getPlayer())) {
			event.getPlayer().sendMessage(
					ChatColor.RED + "No, bad " + event.getPlayer().getName()
							+ "! You can't do that while frozen!");
			event.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPreLogin(final PlayerPreLoginEvent event) {
		IPLogManager.initalize();
		IPLogManager.ipLog.set(event.getName(), event.getAddress().getHostAddress());
		try {
			IPLogManager.ipLog.save(IPLogManager.logFile);
		} catch (final IOException e) {
			Main.LOG.severe(("[DragonList] Error saving IP log file! (" + e.getMessage()) == null ? ""
					: e.getMessage() + ")");
		}
	}
	
	@EventHandler
	public void onQuit(final PlayerQuitEvent event) {
		RestrictionManager.thaw(event.getPlayer());
	}
}
