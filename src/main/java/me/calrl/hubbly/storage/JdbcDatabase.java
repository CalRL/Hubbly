package me.calrl.hubbly.storage;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.pool.HikariPool;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Consumer;

public final class JdbcDatabase implements AutoCloseable {
    private final String jdbcUrl;
    private final String driverClassName;
    private final String username;
    private final String password;
    private final Consumer<HikariConfig> backendConfig;


    private HikariDataSource dataSource;

    public JdbcDatabase(
            String jdbcUrl,
            String driverClassName,
            String username,
            String password
    ) {
        this(
                jdbcUrl,
                driverClassName,
                username,
                password,
                config -> {}
        );
    }

    public JdbcDatabase(
            String jdbcUrl,
            String driverClassName,
            String username,
            String password,
            Consumer<HikariConfig> backendConfig
    ) {
        this.jdbcUrl = jdbcUrl;
        this.driverClassName = driverClassName;
        this.username = username;
        this.password = password;
        this.backendConfig = backendConfig;
    }

    public void connect() throws HikariPool.PoolInitializationException {
        HikariConfig config = new HikariConfig();

        config.setJdbcUrl(this.jdbcUrl);
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        config.setUsername(this.username);
        config.setPassword(this.password);

        config.setMaximumPoolSize(2);
        config.setMinimumIdle(1);
        config.setMaxLifetime(600000);
        config.setConnectionTimeout(5000);
        config.setLeakDetectionThreshold(60000);

        this.backendConfig.accept(config);

        try {
            this.dataSource = new HikariDataSource(config);
        } catch (Exception e) {
            throw new HikariPool.PoolInitializationException(
                    new Exception("Failed to initialize HikariCP connection pool", e)
            );
        }
    }

    public Connection getConnection() throws Exception {
        if(this.isConnected()) {
            return this.dataSource.getConnection();
        }
        throw new Exception("Database connection pool is not available.");
    }

    public boolean isConnected() {
        return dataSource != null && !dataSource.isClosed();
    }

    public void disconnect() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

    @Override
    public void close() throws Exception {
        this.disconnect();
    }
}
