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
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.logging.Logger;

public class StorageManager {
    private volatile AsyncPlayerSaveQueue saveQueue;
    private volatile Database database;
    private volatile CompletableFuture<Void> startupFuture;
    private final Logger logger;
    private final Hubbly plugin;
    private volatile boolean active;
    private volatile boolean shuttingDown;
    private final ConcurrentHashMap<UUID, PlayerData> map;

    public StorageManager(Hubbly plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.map = new ConcurrentHashMap<UUID, PlayerData>();
        logger.info("Initializing storage manager");
        this.startIfEnabled();
    }

    private void startIfEnabled() {
        FileConfiguration config = plugin.getConfig();

        if (!config.getBoolean("database.enabled", false)) {
            logger.info("Database disabled in config; using local storage only");
            active = false;
            return;
        }

        this.start();
    }

    public void start() {
        FileConfiguration config = plugin.getConfig();

        Credentials credentials = Credentials.fromConfig(config);
        Database startupDatabase = new Database(credentials);
        database = startupDatabase;
        active = false;
        shuttingDown = false;

        logger.info("Database enabled, attempting connection...");

        startupFuture = CompletableFuture
                .runAsync(() -> connectAndInitialize(startupDatabase))
                .whenComplete((ignored, error) -> {
                    if (error != null) {
                        handleStartupFailure(error, startupDatabase);
                        return;
                    }

                    finishStartup(startupDatabase);
                });
    }

    /**
     * Fire-and-forget save
     */
    public void enqueueSave(PlayerData snapshot) {
        AsyncPlayerSaveQueue queue = saveQueue;
        if (!active || queue == null) return;

        queue.enqueue(snapshot);
    }

    private void initializeTables(Database startupDatabase) throws SQLException {
        try (Connection conn = startupDatabase.getConnection()) {
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

            try(Statement statement = conn.createStatement()) {
                statement.execute(sql);
            }

            logger.info("Database tables initialized successfully");
        }
    }

    private void handleStartupFailure(Throwable error, Database startupDatabase) {
        logger.warning("Failed to start database storage: " + getRootMessage(error));
        logger.warning("Falling back to PersistentDataContainer storage");
        active = false;
        startupDatabase.disconnect();
        if (database == startupDatabase) {
            database = null;
        }
    }

    private void finishStartup(Database startupDatabase) {
        if (shuttingDown || database != startupDatabase) {
            startupDatabase.disconnect();
            if (database == startupDatabase) {
                database = null;
            }
            return;
        }

        saveQueue = new AsyncPlayerSaveQueue(this::savePlayer);
        active = true;
        logger.info("Database storage is active");
    }

    private void connectAndInitialize(Database startupDatabase) {
        try {
            startupDatabase.connect();

            try (Connection conn = startupDatabase.getConnection()) {
                if (!conn.isValid(2)) {
                    throw new SQLException("Connection validation failed");
                }
            }

            initializeTables(startupDatabase);
        } catch (SQLException e) {
            throw new CompletionException(e);
        }
    }

    public @NotNull PlayerData loadPlayer(UUID uuid, String name) {
        Database currentDatabase = database;
        if (!active || currentDatabase == null) {
            return defaultPlayer(uuid, name);
        }

        try (Connection con = currentDatabase.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "SELECT movement, visibility FROM player_data WHERE uuid = ?"
             )) {

            ps.setString(1, uuid.toString());

            try (var rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new PlayerData(
                            uuid,
                            name,
                            new PlayerMovementData(parseMovementMode(rs.getString("movement"))),
                            new PlayerVisibilityData(parseVisibilityMode(rs.getString("visibility")))
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return defaultPlayer(uuid, name);
        }

        return defaultPlayer(uuid, name);
    }

    private void savePlayer(PlayerData data) {
        Database currentDatabase = database;
        if (currentDatabase == null) {
            return;
        }

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

        try (Connection con = currentDatabase.getConnection();
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

    private PlayerMovementMode parseMovementMode(String value) {
        if (value == null) {
            return PlayerMovementMode.NONE;
        }

        Optional<PlayerMovementMode> mode = PlayerMovementMode.fromString(value);
        if (mode.isEmpty()) {
            logger.warning("Invalid stored player movement mode: " + value);
            return PlayerMovementMode.NONE;
        }
        return mode.get();
    }

    private PlayerVisibilityMode parseVisibilityMode(String value) {
        if (value == null) {
            return PlayerVisibilityMode.VISIBLE;
        }

        try {
            return PlayerVisibilityMode.valueOf(value);
        } catch (IllegalArgumentException e) {
            logger.warning("Invalid stored player visibility mode: " + value);
            return PlayerVisibilityMode.VISIBLE;
        }
    }

    private String getRootMessage(Throwable throwable) {
        Throwable current = throwable;
        while (current.getCause() != null) {
            current = current.getCause();
        }
        return current.getMessage();
    }

    public Result updateMovementMode(Player player, PlayerMovementMode mode) {
        PlayerVisibilityMode visibilityMode = PlayerVisibilityMode.VISIBLE;
        String visibilityValue = this.getVisibilityMode(player);
        if (visibilityValue != null) {
            visibilityMode = PlayerVisibilityMode.valueOf(visibilityValue);
        }
        PlayerVisibilityData pvData = new PlayerVisibilityData(visibilityMode);
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
        PlayerMovementMode movementMode = PlayerMovementMode.NONE;
        String movementValue = this.getMovementMode(player);
        if (movementValue != null) {
            movementMode = parseMovementMode(movementValue);
        }
        PlayerMovementData mvData = new PlayerMovementData(movementMode);
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
        shuttingDown = true;
        active = false;

        CompletableFuture<Void> future = startupFuture;
        if (future != null && !future.isDone()) {
            try {
                future.get(10, TimeUnit.SECONDS);
            } catch (ExecutionException e) {
                logger.warning("Database startup finished during shutdown: " + getRootMessage(e));
            } catch (TimeoutException e) {
                logger.warning("Database startup did not finish within 10s; continuing shutdown");
                future.cancel(true);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        AsyncPlayerSaveQueue queue = saveQueue;
        saveQueue = null;
        if (queue != null) {
            queue.shutdownAndFlush();
        }

        Database currentDatabase = database;
        database = null;
        if (currentDatabase != null) {
            currentDatabase.disconnect();
        }
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
