package com.notpatch.nCore.module;

import com.notpatch.nCore.util.NLogger;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class ModuleConfig {

    private final Module module;
    private final File configFolder;
    private final File configFile;
    private FileConfiguration config;

    public ModuleConfig(Module module) {
        this.module = module;
        this.configFolder = new File(module.getPlugin().getDataFolder(), "modules/" + module.getName());
        this.configFile = new File(configFolder, "config.yml");
    }

    public void load() {
        if (!configFolder.exists()) {
            if (configFolder.mkdirs()) {
                NLogger.debug("Created config folder for module: " + module.getName());
            }
        }

        if (!configFile.exists()) {
            saveDefaultConfig();
        }

        config = YamlConfiguration.loadConfiguration(configFile);
        NLogger.debug("Loaded config for module: " + module.getName());
    }

    public void saveDefaultConfig() {
        if (configFile.exists()) {
            return;
        }

        try {
            Class<? extends Module> moduleClass = module.getClass();
            InputStream resource = moduleClass.getResourceAsStream("/config.yml");

            if (resource == null) {
                NLogger.debug("No default config found for module: " + module.getName() + ", creating empty config");
                config = new YamlConfiguration();
                save();
            } else {
                Files.copy(resource, configFile.toPath());
                resource.close();
                NLogger.debug("Saved default config for module: " + module.getName());
            }
        } catch (IOException e) {
            NLogger.error("Failed to save default config for module: " + module.getName());
            NLogger.exception(e);
        }
    }

    public void save() {
        try {
            config.save(configFile);
            NLogger.debug("Saved config for module: " + module.getName());
        } catch (IOException e) {
            NLogger.error("Failed to save config for module: " + module.getName());
            NLogger.exception(e);
        }
    }

    public void reload() {
        config = YamlConfiguration.loadConfiguration(configFile);
        NLogger.debug("Reloaded config for module: " + module.getName());
    }

    public FileConfiguration getConfig() {
        if (config == null) {
            load();
        }
        return config;
    }

    public File getConfigFolder() {
        return configFolder;
    }

    public File getConfigFile() {
        return configFile;
    }

    public FileConfiguration getCustomConfig(String fileName) {
        File customFile = new File(configFolder, fileName);

        if (!customFile.exists()) {
            try {
                InputStream resource = module.getClass().getResourceAsStream("/" + fileName);
                if (resource != null) {
                    Files.copy(resource, customFile.toPath());
                    resource.close();
                    NLogger.debug("Created custom config file: " + fileName + " for module: " + module.getName());
                } else {
                    YamlConfiguration emptyConfig = new YamlConfiguration();
                    emptyConfig.save(customFile);
                    NLogger.debug("Created empty custom config file: " + fileName + " for module: " + module.getName());
                }
            } catch (IOException e) {
                NLogger.error("Failed to create custom config: " + fileName + " for module: " + module.getName());
                NLogger.exception(e);
            }
        }

        return YamlConfiguration.loadConfiguration(customFile);
    }

    public void saveCustomConfig(String fileName, FileConfiguration config) {
        File customFile = new File(configFolder, fileName);
        try {
            config.save(customFile);
            NLogger.debug("Saved custom config file: " + fileName + " for module: " + module.getName());
        } catch (IOException e) {
            NLogger.error("Failed to save custom config: " + fileName + " for module: " + module.getName());
            NLogger.exception(e);
        }
    }
}

