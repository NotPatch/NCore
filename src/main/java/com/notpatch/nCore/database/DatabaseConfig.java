package com.notpatch.nCore.database;

import org.bukkit.configuration.file.FileConfiguration;

public class DatabaseConfig {

    private String host;
    private int port;
    private String database;
    private String username;
    private String password;
    private int poolSize;
    private boolean useSSL;

    public DatabaseConfig(FileConfiguration config) {
        this.host = config.getString("database.host", "localhost");
        this.port = config.getInt("database.port", 3306);
        this.database = config.getString("database.database", "ncore");
        this.username = config.getString("database.username", "root");
        this.password = config.getString("database.password", "");
        this.poolSize = config.getInt("database.pool-size", 10);
        this.useSSL = config.getBoolean("database.use-ssl", false);
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getDatabase() {
        return database;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getPoolSize() {
        return poolSize;
    }

    public boolean isUseSSL() {
        return useSSL;
    }

    public String getJdbcUrl() {
        return String.format("jdbc:mysql://%s:%d/%s?useSSL=%s&autoReconnect=true&characterEncoding=utf8",
                host, port, database, useSSL);
    }
}

