package com.notpatch.napi;

public class NAPI {

    private static NAPI instance;

    private final String pluginName;

    public NAPI(String pluginName) {
        this.pluginName = pluginName;
        instance = this;
    }

    public void init() {
        NLogger.info("NAPI is initialized!");
    }

    public String getPluginName() {
        return pluginName;
    }

    public static NAPI getInstance() {
        return instance;
    }
}