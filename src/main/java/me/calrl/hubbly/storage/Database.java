package me.calrl.hubbly.storage;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private final Credentials credentials;

    private Connection connection;
    private HikariDataSource dataSource;

    public Database(Credentials credentials) {
        this.credentials = credentials;
    }

    public void connect() throws SQLException {
        HikariConfig config = new HikariConfig();
        String jdbc = String.format("jdbc:mysql://%s:%d/%s",
                credentials.getHOST(),
                credentials.getPORT(),
                credentials.getDATABASE());

        config.setJdbcUrl(jdbc);
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        config.setUsername(credentials.getUSERNAME());
        config.setPassword(credentials.getPASSWORD());

        config.setMaximumPoolSize(1);
        config.setMinimumIdle(2);
        config.setMaxLifetime(600000);
        config.setConnectionTimeout(5000);
        config.setLeakDetectionThreshold(60000);

        config.addDataSourceProperty("cachePrepStmts", true);
        config.addDataSourceProperty("prepStmtCacheSize", 250);
        config.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
        config.addDataSourceProperty("useServerPrepStmts", true);
        config.addDataSourceProperty("useLocalSessionState", true);
        config.addDataSourceProperty("rewriteBatchedStatements", true);
        config.addDataSourceProperty("cacheResultSetMetadata", true);
        config.addDataSourceProperty("cacheServerConfiguration", true);
        config.addDataSourceProperty("elideSetAutoCommits", true);
        config.addDataSourceProperty("maintainTimeStats", false);
        config.addDataSourceProperty("useSSL", false);

        config.setConnectionTestQuery("SELECT 1");

        try {
            this.dataSource = new HikariDataSource(config);
        } catch (Exception e) {
            throw new SQLException("Failed to initialize HikariCP connection pool", e);
        }
    }

    public Connection getConnection() throws SQLException{
        if(this.isConnected()) {
            return this.dataSource.getConnection();
        }
        throw new SQLException("Database connection pool is not available.");
    }

    public boolean isConnected() {
        return dataSource != null && !dataSource.isClosed();
    }

    public void disconnect() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}
