package com.notpatch.napi.integration;

import com.notpatch.napi.NLogger;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class Integration {

    private final String pluginName;
    private final boolean hardDependency;

    public Integration(String pluginName, boolean hardDependency) {
        this.pluginName = pluginName;
        this.hardDependency = hardDependency;
    }

    public boolean isEnabled(){
        return Bukkit.getServer().getPluginManager().getPlugin(pluginName) != null;
    }

    public String getPluginName() {
        return pluginName;
    }

    public void initialize(JavaPlugin plugin) {
        if (isEnabled()) {
            setup();
            NLogger.info("Successfully hooked into " + pluginName + "!");
        } else if (hardDependency) {
            Bukkit.getPluginManager().disablePlugin(plugin);
            throw new IllegalStateException("Required plugin '" + pluginName + "' is not installed!");
        }
    }

    protected abstract void setup();
}
