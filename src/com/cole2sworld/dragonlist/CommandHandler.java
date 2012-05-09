package com.cole2sworld.dragonlist;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;

public class CommandHandler {
	private static List<String> badPasswords = Arrays.asList("12345", "password", "p4$$w0rd", "pa$$w0rd", "pa$$word", "p455w0rd", "54321");
	public void add(CommandSender sender, String[] args, String label) {
		try {
			if (GlobalConf.mode != WhitelistMode.IP) {
				InetAddress.getByName(args[0]);
				sender.sendMessage(ChatColor.RED+"You can't whitelist IPs outside of IP whitelist mode!");
				return;
			}
		} catch (UnknownHostException e) {
			// exception means success
		}
		if (GlobalConf.mode == WhitelistMode.NAME) WhitelistManager.addToNameWhitelist(args[0]);
		if (GlobalConf.mode == WhitelistMode.IP) WhitelistManager.addToIPWhitelist(IPLogManager.lookupByName(args[0]));
		if (GlobalConf.mode == WhitelistMode.PASSWORD) WhitelistManager.addToPasswordedWhitelist(args[0]);
		sender.sendMessage(ChatColor.AQUA+args[0]+" added to the "+GlobalConf.mode.toString()+" whitelist.");
	}
	public void export(CommandSender sender, String[] args, String label) {
		for (String name : WhitelistManager.getWhitelistedNames()) {
			Bukkit.getServer().getOfflinePlayer(name).setWhitelisted(true);
			sender.sendMessage(ChatColor.GREEN+"Exported "+name);
		}
		sender.sendMessage(ChatColor.AQUA+"Finished!");
	}
	public void _import(CommandSender sender, String[] args, String label) {
		Server server = Bukkit.getServer();
		for (OfflinePlayer player : server.getWhitelistedPlayers()) {
			WhitelistManager.addToNameWhitelist(player.getName());
			sender.sendMessage(ChatColor.GREEN+"Imported "+player.getName());
		}
		sender.sendMessage(ChatColor.AQUA+"Finished!");
	}
	public void remove(CommandSender sender, String[] args, String label) {
		try {
			if (GlobalConf.mode != WhitelistMode.IP) {
				InetAddress.getByName(args[0]);
				sender.sendMessage(ChatColor.RED+"You can't whitelist IPs outside of IP whitelist mode!");
				return;
			}
		} catch (UnknownHostException e) {
			// exception means success
		}
		if (GlobalConf.mode == WhitelistMode.NAME) WhitelistManager.removeFromNameWhitelist(args[0]);
		if (GlobalConf.mode == WhitelistMode.IP) WhitelistManager.removeFromIPWhitelist(IPLogManager.lookupByName(args[0]));
		if (GlobalConf.mode == WhitelistMode.PASSWORD) WhitelistManager.removeFromPasswordWhitelist(args[0]);
		sender.sendMessage(ChatColor.AQUA+args[0]+" removed from the "+GlobalConf.mode.toString()+" whitelist.");
	}
	public void setpass(CommandSender sender, String[] args, String label) {
		if (args.length == 0) {
			sender.sendMessage(ChatColor.RED+"Please specify a password!");
			return;
		}
		if (badPasswords.contains(args[0].toLowerCase())) {
			sender.sendMessage(ChatColor.RED+"Invalid password!");
			return;
		}
		StringBuilder builder = new StringBuilder();
		for (String part : args) {
			builder.append(part);
			builder.append(" ");
		}
		builder.deleteCharAt(builder.length()-1);
		AuthManager.changePassword(sender.getName(), builder.toString());
		sender.sendMessage(ChatColor.AQUA+"Password set to '"+builder.toString()+"'");
	}
}
