package com.notpatch.nCore.module;

import com.notpatch.nCore.NCore;
import com.notpatch.nCore.database.DatabaseManager;
import org.bukkit.configuration.file.FileConfiguration;

public abstract class Module {

    private NCore plugin;
    private ModuleData moduleData;
    private DatabaseManager databaseManager;
    private ModuleConfig moduleConfig;
    private boolean enabled = false;

    public abstract void onLoad();

    public abstract void onEnable();

    public abstract void onDisable();

    public void onReload() {
        if (enabled) {
            onDisable();
            if (moduleConfig != null) {
                moduleConfig.reload();
            }
            onEnable();
        }
    }

    public void setPlugin(NCore plugin) {
        this.plugin = plugin;
        this.moduleConfig = new ModuleConfig(this);
    }

    public NCore getPlugin() {
        return plugin;
    }

    public void setModuleData(ModuleData moduleData) {
        this.moduleData = moduleData;
    }

    public ModuleData getModuleData() {
        return moduleData;
    }

    public void setDatabaseManager(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public boolean hasDatabase() {
        return databaseManager != null && databaseManager.isConnected();
    }

    public boolean isEnabled() {
        return enabled;
    }

    protected void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getName() {
        return moduleData != null ? moduleData.getName() : "Unknown";
    }

    public void loadConfig() {
        if (moduleConfig != null) {
            moduleConfig.load();
        }
    }

    public void saveConfig() {
        if (moduleConfig != null) {
            moduleConfig.save();
        }
    }

    public void saveDefaultConfig() {
        if (moduleConfig != null) {
            moduleConfig.saveDefaultConfig();
        }
    }

    public void reloadConfig() {
        if (moduleConfig != null) {
            moduleConfig.reload();
        }
    }

    public FileConfiguration getConfig() {
        if (moduleConfig == null) {
            return null;
        }
        return moduleConfig.getConfig();
    }

    public FileConfiguration getCustomConfig(String fileName) {
        if (moduleConfig == null) {
            return null;
        }
        return moduleConfig.getCustomConfig(fileName);
    }

    public void saveCustomConfig(String fileName, FileConfiguration config) {
        if (moduleConfig != null) {
            moduleConfig.saveCustomConfig(fileName, config);
        }
    }

    public java.io.File getDataFolder() {
        if (moduleConfig == null) {
            return null;
        }
        return moduleConfig.getConfigFolder();
    }
}

