package com.notpatch.nCore.module;

import com.notpatch.nCore.NCore;
import com.notpatch.nCore.util.NLogger;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ModuleLoader {

    private final NCore plugin;

    public ModuleLoader(NCore plugin) {
        this.plugin = plugin;
    }

    public List<ModuleData> loadModulesFromDirectory(File directory) {
        List<ModuleData> loadedModules = new ArrayList<>();

        if (!directory.exists()) {
            if (directory.mkdirs()) {
                NLogger.debug("Created modules directory: " + directory.getPath());
            } else {
                NLogger.error("Failed to create modules directory: " + directory.getPath());
            }
            return loadedModules;
        }

        File[] files = directory.listFiles((dir, name) -> name.endsWith(".jar"));
        if (files == null || files.length == 0) {
            NLogger.debug("No JAR files found in modules directory.");
            return loadedModules;
        }

        for (File file : files) {
            try {
                ModuleData moduleData = loadModuleFromJar(file);
                if (moduleData != null) {
                    loadedModules.add(moduleData);
                }
            } catch (Exception e) {
                NLogger.error("Failed to load module from: " + file.getName());
                NLogger.exception(e);
            }
        }

        return loadedModules;
    }

    public ModuleData loadModuleFromJar(File jarFile) throws Exception {
        URLClassLoader classLoader = new URLClassLoader(
                new URL[]{jarFile.toURI().toURL()},
                plugin.getClass().getClassLoader()
        );

        try (JarFile jar = new JarFile(jarFile)) {
            Enumeration<JarEntry> entries = jar.entries();

            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String name = entry.getName();

                if (name.endsWith(".class")) {
                    String className = name.replace("/", ".").replace(".class", "");

                    try {
                        Class<?> clazz = classLoader.loadClass(className);

                        if (Module.class.isAssignableFrom(clazz) && !clazz.isInterface() && clazz.isAnnotationPresent(ModuleInfo.class)) {
                            @SuppressWarnings("unchecked")
                            Class<? extends Module> moduleClass = (Class<? extends Module>) clazz;
                            ModuleInfo info = moduleClass.getAnnotation(ModuleInfo.class);

                            return new ModuleData(info, jarFile, moduleClass);
                        }
                    } catch (ClassNotFoundException | NoClassDefFoundError ignored) {
                    }
                }
            }
        }

        NLogger.debug("No valid module found in JAR: " + jarFile.getName());
        return null;
    }

    public Module createModuleInstance(ModuleData moduleData) throws Exception {
        Module module = moduleData.getModuleClass().getDeclaredConstructor().newInstance();
        module.setPlugin(plugin);
        module.setModuleData(moduleData);
        return module;
    }
}

