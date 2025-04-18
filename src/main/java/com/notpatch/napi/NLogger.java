package com.notpatch.napi;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class NLogger {

    private static String getPrefix() {
        String name = NAPI.getInstance() != null ? NAPI.getInstance().getPluginName() : "Plugin";
        return "§7[§f" + name + "§7] ";
    }

    public static void info(String message) {
        Bukkit.getConsoleSender().sendMessage(getPrefix() + ChatColor.GREEN + message);
    }

    public static void warn(String message) {
        Bukkit.getConsoleSender().sendMessage(getPrefix() + ChatColor.YELLOW + message);
    }

    public static void error(String message) {
        Bukkit.getConsoleSender().sendMessage(getPrefix() + ChatColor.RED + message);
    }
}