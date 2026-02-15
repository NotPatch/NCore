package com.notpatch.nCore.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class NLogger {

    private static boolean debugMode = false;

    public static void setDebugMode(boolean debug) {
        debugMode = debug;
    }

    public static void info(String message) {
        Bukkit.getConsoleSender().sendMessage("§7[§fNCore§7] " + ChatColor.GREEN + message);
    }

    public static void debug(String message) {
        if (debugMode) {
            Bukkit.getConsoleSender().sendMessage("§7[§fNCore§7] §8[DEBUG] " + ChatColor.GRAY + message);
        }
    }

    public static void warn(String message) {
        Bukkit.getConsoleSender().sendMessage("§7[§fNCore§7] " + ChatColor.YELLOW + message);
    }

    public static void error(String message) {
        Bukkit.getConsoleSender().sendMessage("§7[§fNCore§7] " + ChatColor.RED + message);
    }

    public static void exception(Exception e){
        Bukkit.getConsoleSender().sendMessage("§7[§fNCore§7] | " + e.getMessage());
    }

}