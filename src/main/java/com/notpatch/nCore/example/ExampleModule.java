package com.notpatch.nCore.example;

import com.notpatch.nCore.module.Module;
import com.notpatch.nCore.module.ModuleInfo;
import com.notpatch.nCore.util.NLogger;
import org.bukkit.configuration.file.FileConfiguration;

@ModuleInfo(
        name = "ExampleModule",
        version = "1.0.0",
        description = "Example module showing config usage",
        authors = {"NotPatch"},
        requiresDatabase = false
)
public class ExampleModule extends Module {

    private boolean debugMode;
    private String prefix;
    private int customValue;

    @Override
    public void onLoad() {
        NLogger.info("Loading " + getName() + "...");
        loadConfig();
        loadConfigValues();
        NLogger.info(getName() + " loaded!");
    }

    @Override
    public void onEnable() {
        NLogger.info("Enabling " + getName() + "...");

        if (debugMode) {
            NLogger.debug("Debug mode is enabled for " + getName());
        }

        NLogger.info(getName() + " enabled with prefix: " + prefix);
        NLogger.info("Custom value: " + customValue);
    }

    @Override
    public void onDisable() {
        NLogger.info("Disabling " + getName() + "...");
        saveConfig();
        NLogger.info(getName() + " disabled!");
    }

    @Override
    public void onReload() {
        NLogger.info("Reloading " + getName() + "...");
        reloadConfig();
        loadConfigValues();
        NLogger.info(getName() + " reloaded!");
    }

    private void loadConfigValues() {
        FileConfiguration config = getConfig();

        if (config == null) {
            NLogger.warn("Config is null for " + getName());
            return;
        }

        debugMode = config.getBoolean("debug", false);
        prefix = config.getString("messages.prefix", "&7[&eExampleModule&7]");
        customValue = config.getInt("features.custom-value", 100);

        NLogger.debug("Loaded config values for " + getName());
    }

    public void loadCustomConfig() {
        FileConfiguration messagesConfig = getCustomConfig("messages.yml");

        if (messagesConfig != null) {
            String welcomeMessage = messagesConfig.getString("welcome", "Welcome!");
            NLogger.info("Welcome message from custom config: " + welcomeMessage);
        }

        FileConfiguration dbConfig = getCustomConfig("database.yml");

        if (dbConfig != null) {
            String host = dbConfig.getString("host", "localhost");
            int port = dbConfig.getInt("port", 3306);
            NLogger.info("Database config: " + host + ":" + port);
        }
    }

    public void updateConfigValue(String path, Object value) {
        FileConfiguration config = getConfig();

        if (config != null) {
            config.set(path, value);
            saveConfig();
            NLogger.info("Updated config value: " + path + " = " + value);
        }
    }
}

