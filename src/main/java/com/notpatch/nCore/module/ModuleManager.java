package com.notpatch.nCore.module;

import com.notpatch.nCore.NCore;
import com.notpatch.nCore.database.DatabaseManager;
import com.notpatch.nCore.util.NLogger;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class ModuleManager {

    private final NCore plugin;
    private final ModuleLoader moduleLoader;
    private final Map<String, Module> modules;
    private final File modulesDirectory;

    public ModuleManager(NCore plugin) {
        this.plugin = plugin;
        this.moduleLoader = new ModuleLoader(plugin);
        this.modules = new LinkedHashMap<>();
        this.modulesDirectory = new File(plugin.getDataFolder(), "modules");
    }

    public void loadModules() {
        List<ModuleData> moduleDataList = moduleLoader.loadModulesFromDirectory(modulesDirectory);

        if (moduleDataList.isEmpty()) {
            NLogger.warn("No modules found to load.");
            return;
        }

        List<ModuleData> sortedModules = sortByDependencies(moduleDataList);

        for (ModuleData moduleData : sortedModules) {
            try {
                Module module = moduleLoader.createModuleInstance(moduleData);
                modules.put(moduleData.getName(), module);
                module.onLoad();
                NLogger.debug("Loaded module: " + moduleData.getName());
            } catch (Exception e) {
                NLogger.error("Failed to load module: " + moduleData.getName());
                NLogger.exception(e);
            }
        }

        NLogger.info("Loaded " + modules.size() + " module(s).");
    }

    public void enableModules() {

        int enabledCount = 0;
        for (Module module : modules.values()) {
            if (enableModule(module)) {
                enabledCount++;
                NLogger.debug("Enabled module: " + module.getName());
            }
        }

        NLogger.info("Enabled " + enabledCount + " module(s).");
    }

    public boolean enableModule(Module module) {
        if (module.isEnabled()) {
            return false;
        }

        for (String dependency : module.getModuleData().getDependencies()) {
            Module depModule = modules.get(dependency);
            if (depModule == null || !depModule.isEnabled()) {
                NLogger.error("Cannot enable " + module.getName() + ": Missing dependency " + dependency);
                return false;
            }
        }

        if (module.getModuleData().requiresDatabase()) {
            if (plugin.getDatabaseManager() == null) {
                DatabaseManager dbManager = new DatabaseManager(plugin.getDatabaseConfig());
                if (!dbManager.connect()) {
                    NLogger.error("Cannot enable " + module.getName() + ": Database connection failed");
                    return false;
                }
                plugin.setDatabaseManager(dbManager);
            }
            module.setDatabaseManager(plugin.getDatabaseManager());
        }

        try {
            module.onEnable();
            module.setEnabled(true);
            return true;
        } catch (Exception e) {
            NLogger.error("Failed to enable module: " + module.getName());
            NLogger.exception(e);
            return false;
        }
    }

    public boolean enableModule(String name) {
        Module module = modules.get(name);
        if (module == null) {
            NLogger.error("Module not found: " + name);
            return false;
        }
        return enableModule(module);
    }

    public boolean disableModule(Module module) {
        if (!module.isEnabled()) {
            return false;
        }

        try {
            module.onDisable();
            module.setEnabled(false);
            return true;
        } catch (Exception e) {
            NLogger.error("Failed to disable module: " + module.getName());
            NLogger.exception(e);
            return false;
        }
    }

    public boolean disableModule(String name) {
        Module module = modules.get(name);
        if (module == null) {
            NLogger.error("Module not found: " + name);
            return false;
        }
        return disableModule(module);
    }

    public void disableModules() {

        List<Module> moduleList = new ArrayList<>(modules.values());
        Collections.reverse(moduleList);

        for (Module module : moduleList) {
            if (module.isEnabled()) {
                disableModule(module);
                NLogger.debug("Disabled module: " + module.getName());
            }
        }

        NLogger.info("All modules disabled.");
    }

    public boolean reloadModule(String name) {
        Module module = modules.get(name);
        if (module == null) {
            NLogger.error("Module not found: " + name);
            return false;
        }

        try {
            module.onReload();
            return true;
        } catch (Exception e) {
            NLogger.error("Failed to reload module: " + module.getName());
            NLogger.exception(e);
            return false;
        }
    }

    public void reloadModules() {
        disableModules();
        modules.clear();
        loadModules();
        enableModules();
        NLogger.info("All modules reloaded.");
    }

    public boolean loadModuleFromFile(String jarFileName) {
        String moduleName = jarFileName.replace(".jar", "");
        if (modules.containsKey(moduleName)) {
            NLogger.warn("Module " + moduleName + " is already loaded.");
            return false;
        }

        File jarFile = new File(modulesDirectory, jarFileName);
        if (!jarFile.exists() || !jarFile.getName().endsWith(".jar")) {
            NLogger.error("JAR file not found: " + jarFileName);
            return false;
        }

        try {
            ModuleData moduleData = moduleLoader.loadModuleFromJar(jarFile);
            if (moduleData == null) {
                return false;
            }

            for (String dependency : moduleData.getDependencies()) {
                if (!modules.containsKey(dependency)) {
                    NLogger.error("Cannot load " + moduleData.getName() + ": Missing dependency " + dependency);
                    return false;
                }
            }

            Module module = moduleLoader.createModuleInstance(moduleData);
            modules.put(moduleData.getName(), module);

            module.onLoad();
            NLogger.info("Loaded module: " + moduleData.getName() + " v" + moduleData.getVersion());

            if (enableModule(module)) {
                NLogger.info("Enabled module: " + moduleData.getName());
                return true;
            }

            return false;
        } catch (Exception e) {
            NLogger.error("Failed to load module from: " + jarFileName);
            NLogger.exception(e);
            return false;
        }
    }

    public Module getModule(String name) {
        return modules.get(name);
    }

    public Collection<Module> getModules() {
        return modules.values();
    }

    public List<Module> getEnabledModules() {
        return modules.values().stream()
                .filter(Module::isEnabled)
                .collect(Collectors.toList());
    }

    public boolean isModuleLoaded(String name) {
        return modules.containsKey(name);
    }

    public File getModulesDirectory() {
        return modulesDirectory;
    }

    private List<ModuleData> sortByDependencies(List<ModuleData> moduleDataList) {
        List<ModuleData> sorted = new ArrayList<>();
        Set<String> processed = new HashSet<>();
        Map<String, ModuleData> moduleMap = moduleDataList.stream()
                .collect(Collectors.toMap(ModuleData::getName, data -> data));

        for (ModuleData data : moduleDataList) {
            sortModule(data, moduleMap, sorted, processed, new HashSet<>());
        }

        return sorted;
    }

    private void sortModule(ModuleData moduleData, Map<String, ModuleData> moduleMap,
                           List<ModuleData> sorted, Set<String> processed, Set<String> visiting) {
        if (processed.contains(moduleData.getName())) {
            return;
        }

        if (visiting.contains(moduleData.getName())) {
            NLogger.warn("Circular dependency detected for module: " + moduleData.getName());
            return;
        }

        visiting.add(moduleData.getName());

        for (String dependency : moduleData.getDependencies()) {
            ModuleData depData = moduleMap.get(dependency);
            if (depData != null) {
                sortModule(depData, moduleMap, sorted, processed, visiting);
            } else {
                NLogger.warn("Module " + moduleData.getName() + " depends on " + dependency + " which is not loaded.");
            }
        }

        visiting.remove(moduleData.getName());
        processed.add(moduleData.getName());
        sorted.add(moduleData);
    }
}

