package com.notpatch.nCore.database;

import com.notpatch.nCore.util.NLogger;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseManager {

    private final DatabaseConfig config;
    private HikariDataSource dataSource;
    private boolean connected = false;

    public DatabaseManager(DatabaseConfig config) {
        this.config = config;
    }

    public boolean connect() {
        try {
            HikariConfig hikariConfig = new HikariConfig();
            hikariConfig.setJdbcUrl(config.getJdbcUrl());
            hikariConfig.setUsername(config.getUsername());
            hikariConfig.setPassword(config.getPassword());
            hikariConfig.setMaximumPoolSize(config.getPoolSize());
            hikariConfig.setConnectionTimeout(30000);
            hikariConfig.setIdleTimeout(600000);
            hikariConfig.setMaxLifetime(1800000);
            hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
            hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
            hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

            this.dataSource = new HikariDataSource(hikariConfig);
            this.connected = true;

            testConnection();
            return true;
        } catch (Exception e) {
            NLogger.error("Failed to connect to database!");
            NLogger.exception(e);
            this.connected = false;
            return false;
        }
    }

    public void disconnect() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            this.connected = false;
        }
    }

    public Connection getConnection() throws SQLException {
        if (!connected || dataSource == null) {
            throw new SQLException("Database is not connected!");
        }
        return dataSource.getConnection();
    }

    public boolean isConnected() {
        return connected && dataSource != null && !dataSource.isClosed();
    }

    private void testConnection() throws SQLException {
        try (Connection connection = getConnection()) {
            if (!connection.isValid(5)) {
                throw new SQLException("Database connection test failed!");
            }
        }
    }

    public void executeUpdate(String sql, Object... params) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }

            stmt.executeUpdate();
        }
    }

    public ResultSet executeQuery(String sql, Object... params) throws SQLException {
        Connection conn = getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);

        for (int i = 0; i < params.length; i++) {
            stmt.setObject(i + 1, params[i]);
        }

        return stmt.executeQuery();
    }
}

