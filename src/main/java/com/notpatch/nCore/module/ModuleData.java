package com.notpatch.nCore.module;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class ModuleData {

    private final String name;
    private final String version;
    private final String description;
    private final List<String> authors;
    private final List<String> dependencies;
    private final List<String> softDependencies;
    private final boolean requiresDatabase;
    private final File file;
    private final Class<? extends Module> moduleClass;

    public ModuleData(ModuleInfo info, File file, Class<? extends Module> moduleClass) {
        this.name = info.name();
        this.version = info.version();
        this.description = info.description();
        this.authors = Arrays.asList(info.authors());
        this.dependencies = Arrays.asList(info.dependencies());
        this.softDependencies = Arrays.asList(info.softDependencies());
        this.requiresDatabase = info.requiresDatabase();
        this.file = file;
        this.moduleClass = moduleClass;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public List<String> getDependencies() {
        return dependencies;
    }

    public List<String> getSoftDependencies() {
        return softDependencies;
    }

    public boolean requiresDatabase() {
        return requiresDatabase;
    }

    public File getFile() {
        return file;
    }

    public Class<? extends Module> getModuleClass() {
        return moduleClass;
    }

    @Override
    public String toString() {
        return "ModuleData{" +
                "name='" + name + '\'' +
                ", version='" + version + '\'' +
                ", authors=" + authors +
                ", file=" + file.getName() +
                '}';
    }
}

