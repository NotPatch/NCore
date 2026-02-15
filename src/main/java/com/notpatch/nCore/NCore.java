package com.notpatch.nCore;

import com.notpatch.nCore.command.ModuleCommand;
import com.notpatch.nCore.database.DatabaseConfig;
import com.notpatch.nCore.database.DatabaseManager;
import com.notpatch.nCore.module.ModuleManager;
import com.notpatch.nCore.util.NLogger;
import org.bukkit.plugin.java.JavaPlugin;

public final class NCore extends JavaPlugin {

    private static NCore instance;
    private ModuleManager moduleManager;
    private DatabaseConfig databaseConfig;
    private DatabaseManager databaseManager;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();

        NLogger.setDebugMode(getConfig().getBoolean("debug-mode", false));

        databaseConfig = new DatabaseConfig(getConfig());

        moduleManager = new ModuleManager(this);

        moduleManager.loadModules();
        moduleManager.enableModules();

        registerCommand("module", new ModuleCommand(this));

    }

    @Override
    public void onDisable() {

        if (moduleManager != null) {
            moduleManager.disableModules();
        }

        if (databaseManager != null) {
            databaseManager.disconnect();
        }

    }

    public static NCore getInstance() {
        return instance;
    }

    public ModuleManager getModuleManager() {
        return moduleManager;
    }

    public DatabaseConfig getDatabaseConfig() {
        return databaseConfig;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public void setDatabaseManager(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }
}
