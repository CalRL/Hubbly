package me.calrl.hubbly.managers;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.enums.PluginKeys;
import me.calrl.hubbly.enums.Result;
import me.calrl.hubbly.enums.data.PlayerMovementMode;
import me.calrl.hubbly.enums.data.PlayerVisibilityMode;
import me.calrl.hubbly.storage.*;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Async;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class StorageManager {
    private AsyncPlayerSaveQueue saveQueue;
    private Database database;
    private final Logger logger;
    private final Hubbly plugin;
    private boolean active;
    private final ConcurrentHashMap<UUID, PlayerData> map;

    public StorageManager(Hubbly plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.map = new ConcurrentHashMap<UUID, PlayerData>();
        logger.info("Initializing storage manager");
        this.start();
    }

    public void start() {
        FileConfiguration config = plugin.getConfig();

        if (!config.getBoolean("database.enabled", false)) {
            logger.info("Database disabled in config; using local storage only");
            active = false;
            return;
        }

        Credentials credentials = Credentials.fromConfig(config);
        database = new Database(credentials);

        logger.info("Database enabled, attempting connection...");

        try {
            database.connect();

            try (Connection conn = database.getConnection()) {
                if (!conn.isValid(2)) {
                    throw new SQLException("Connection validation failed");
                }
            }

            saveQueue = new AsyncPlayerSaveQueue(this::savePlayer);
            active = true;

            logger.info("Successfully connected to MySQL database");
            initializeTables();

        } catch (SQLException e) {
            logger.warning("Failed to connect to database: " + e.getMessage());
            logger.warning("Falling back to PersistentDataContainer storage");
            active = false;
            database = null;
        }
    }

    /**
     * Fire-and-forget save
     */
    public void enqueueSave(PlayerData snapshot) {
        if (!active) return;

        saveQueue.enqueue(snapshot);
    }

    public PlayerData loadPlayer(UUID uuid, String name) {
        if (!active) {
            return defaultPlayer(uuid, name);
        }

        try (Connection con = database.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "SELECT movement, visibility FROM player_data WHERE uuid = ?"
             )) {

            ps.setString(1, uuid.toString());

            try (var rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new PlayerData(
                            uuid,
                            name,
                            new PlayerMovementData(
                                    PlayerMovementMode.valueOf(rs.getString("movement"))
                            ),
                            new PlayerVisibilityData(
                                    PlayerVisibilityMode.valueOf(rs.getString("visibility"))
                            )
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        return null;
    }

    private void savePlayer(PlayerData data) {
        new DebugMode(plugin).info(String.format("Saving PlayerData for player: %s, %s", data.getName(), data));
        final String sql = """
        INSERT INTO player_data (uuid, name, movement, visibility, last_seen)
        VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)
        ON DUPLICATE KEY UPDATE
            name = VALUES(name),
            movement = VALUES(movement),
            visibility = VALUES(visibility),
            last_seen = CURRENT_TIMESTAMP
        """;

        try (Connection con = database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, data.getUuid().toString());
            ps.setString(2, data.getName());
            ps.setString(3, data.movement().getMode().name());
            ps.setString(4, data.visibility().getMode().name());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void initializeTables() {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Connection conn = database.getConnection()) {
                String sql = """
                    CREATE TABLE IF NOT EXISTS player_data (
                        uuid VARCHAR(36) PRIMARY KEY,
                        name VARCHAR(16) NOT NULL,
                        movement VARCHAR(16),
                        visibility VARCHAR(16),
                        last_seen TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        INDEX idx_name (name)
                    )
                    """;

                conn.createStatement().execute(sql);
                logger.info("Database tables initialized successfully");

            } catch (SQLException e) {
                logger.severe("Failed to initialize database tables!");
                e.printStackTrace();
            }
        });
    }

    public Result updateMovementMode(Player player, PlayerMovementMode mode) {
        PlayerVisibilityData pvData = new PlayerVisibilityData(PlayerVisibilityMode.valueOf(this.getVisibilityMode(player)));
        PlayerData data = new PlayerData(
            player.getUniqueId(),
            player.getName(),
            new PlayerMovementData(mode),
            pvData
        );

        this.enqueueSave(data);
        return Result.SUCCESS;
    }

    public Result updateVisibilityMode(Player player, PlayerVisibilityMode mode) {
        PlayerMovementData mvData = new PlayerMovementData(PlayerMovementMode.valueOf(this.getMovementMode(player)));
        PlayerData data = new PlayerData(
                player.getUniqueId(),
                player.getName(),
                mvData,
                new PlayerVisibilityData(mode)
        );
        this.enqueueSave(data);
        return Result.SUCCESS;
    }

    private String getMovementMode(Player player) {
        PersistentDataContainer container = player.getPersistentDataContainer();

        NamespacedKey key = PluginKeys.MOVEMENT_KEY.getKey();
        if(container.has(key)) {
            return container.get(key, PersistentDataType.STRING);
        }
        return null;
    }

    private String getVisibilityMode(Player player) {
        PersistentDataContainer container = player.getPersistentDataContainer();

        NamespacedKey key = PluginKeys.PLAYER_VISIBILITY.getKey();
        if(container.has(key)) {
            return container.get(key, PersistentDataType.STRING);
        }
        return null;
    }

    public void shutdown() {
        if (saveQueue != null) {
            saveQueue.shutdownAndFlush();
        }
        if (database != null) {
            database.disconnect();
        }
        active = false;
        logger.info("StorageManager shut down");
    }

    private PlayerData defaultPlayer(UUID uuid, String name) {
        return new PlayerData(
                uuid,
                name,
                new PlayerMovementData(PlayerMovementMode.NONE),
                new PlayerVisibilityData(PlayerVisibilityMode.VISIBLE)
        );
    }

    public boolean isActive() {
        return this.active;
    }

    public void addToMap(UUID uuid, PlayerData data) {
        this.map.put(uuid, data);
    }

    public PlayerData getAndRemove(UUID uuid) {
        return this.map.remove(uuid);
    }
}
