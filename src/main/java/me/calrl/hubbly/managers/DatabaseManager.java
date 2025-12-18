package me.calrl.hubbly.managers;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.storage.Credentials;
import me.calrl.hubbly.storage.Database;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseManager {
    private Database database;
    private final Hubbly plugin;
    private final Credentials credentials;
    private boolean isActive;

    public DatabaseManager(Hubbly plugin) {
        this.plugin = plugin;
        this.credentials = this.getCredentials(plugin.getConfig());
    }

    public boolean isActive() {
        return this.isActive;
    }

    private Credentials getCredentials(FileConfiguration config) {
        String host = config.getString("database.host", "localhost");
        Integer port = config.getInt("database.port", 3306);
        String database = config.getString("database.database", "minecraft");
        String username = config.getString("database.username", "root");
        String password = config.getString("database.password", "");
        return new Credentials(host, port, database, username, password);
    }

    public void start(Hubbly plugin) {
        if (!plugin.getConfig().getBoolean("database.enabled", false)) {
            plugin.getLogger().info("Database disabled in config");
            this.isActive = false;
            return;
        }

        plugin.getLogger().info("Database enabled, attempting connection...");
        database = new Database(this.credentials);

        try {
            database.connect();

            // Test the connection
            try (Connection conn = database.getConnection()) {
                if (conn.isValid(2)) {
                    this.isActive = true;
                    plugin.getLogger().info("Successfully connected to MySQL database!");
                    plugin.getLogger().info("HikariCP connection pool initialized");

                    // Initialize tables asynchronously
                    initializeTables();
                } else {
                    throw new SQLException("Connection validation failed");
                }
            }

        } catch (SQLException e) {
            plugin.getLogger().warning("Failed to connect to database: " + e.getMessage());
            plugin.getLogger().warning("Falling back to PersistentDataContainer storage");
            this.isActive = false;
            this.database = null;
        }
    }

    public void stop() {
        if (database != null) {
            plugin.getLogger().info("Closing database connections...");
            database.disconnect();
            this.isActive = false;
        }
    }

    public Database getDatabase() {
        return database;
    }

    private void initializeTables() {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Connection conn = database.getConnection()) {
                String sql = "CREATE TABLE IF NOT EXISTS player_data (" +
                        "uuid VARCHAR(36) PRIMARY KEY, " +
                        "name VARCHAR(16) NOT NULL, " +
                        "double_jump_nbt TEXT, " +
                        "player_visibility_nbt TEXT, " +
                        "last_seen TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                        "INDEX idx_name (name)" +
                        ")";

                conn.createStatement().execute(sql);
                plugin.getLogger().info("Database tables initialized successfully");

            } catch (SQLException e) {
                plugin.getLogger().severe("Failed to initialize database tables!");
                e.printStackTrace();
            }
        });
    }

    /**
     * Execute a database task asynchronously
     */
    public void executeAsync(Runnable task) {
        if (isActive) {
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, task);
        }
    }
}