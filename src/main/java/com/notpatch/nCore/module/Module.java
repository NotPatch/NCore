package com.notpatch.nCore.module;

import com.notpatch.nCore.NCore;
import com.notpatch.nCore.database.DatabaseManager;

public abstract class Module {

    private NCore plugin;
    private ModuleData moduleData;
    private DatabaseManager databaseManager;
    private boolean enabled = false;

    public abstract void onLoad();

    public abstract void onEnable();

    public abstract void onDisable();

    public void onReload() {
        if (enabled) {
            onDisable();
            onEnable();
        }
    }

    public void setPlugin(NCore plugin) {
        this.plugin = plugin;
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
}

