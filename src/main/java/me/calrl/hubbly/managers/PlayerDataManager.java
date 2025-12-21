package me.calrl.hubbly.managers;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.enums.NBTKeys;
import me.calrl.hubbly.storage.AsyncSaveQueue;
import me.calrl.hubbly.storage.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

public class PlayerDataManager {
    private final Hubbly plugin;
    private final HashMap<UUID, PlayerData> dataMap;
    private final AsyncSaveQueue saveQueue;
    private final NamespacedKey doubleJumpKey;
    private final NamespacedKey visibilityKey;

    public PlayerDataManager(Hubbly plugin) {
        this.plugin = plugin;
        this.dataMap = new HashMap<>();
        this.saveQueue = new AsyncSaveQueue(plugin, this);
        this.doubleJumpKey = new NamespacedKey(plugin, "double_jump");
        this.visibilityKey = new NamespacedKey(plugin, "player_visibility");
    }

    /**
     * Get player data from cache
     */
    public PlayerData get(UUID uuid) {
        return dataMap.get(uuid);
    }

//    /**
//     * Update player data in cache and apply to PDC
//     * Marks as dirty and queues for DB save
//     */
//    public void update(UUID uuid, PlayerData newData) {
//        PlayerData old = dataMap.get(uuid);
//        if (old == null) {
//            plugin.getLogger().warning("Attempted to update data for offline player: " + uuid);
//            return;
//        }
//
//        boolean changed = false;
//
//        // Check if double jump changed
//        String oldDoubleJump = old.getDoubleJumpNbt();
//        String newDoubleJump = newData.getDoubleJumpNbt();
//        if ((oldDoubleJump == null && newDoubleJump != null) ||
//                (oldDoubleJump != null && !oldDoubleJump.equals(newDoubleJump))) {
//            this.updateDoubleJump(newData);
//            changed = true;
//        }
//
//        // Check if visibility changed
//        boolean oldVisibility = old.getHideMode();
//        boolean newVisibility = newData.getHideMode();
//        if (oldVisibility != newVisibility) {
//            this.updateVisibility(newData.getUuid());
//            changed = true;
//        }
//
//        // Update cache
//        dataMap.put(uuid, newData);
//
//        // Queue save if data changed
//        if (changed) {
//            onChange(newData);
//        }
//    }

    /**
     * Apply double jump NBT to player's PDC
     */
    private void setDoubleJumpState(UUID uuid, NBTKeys newState) {
        PlayerData data = this.get(uuid);
        if (data == null) return;

        boolean changed = data.setDoubleJumpNbt(newState.get());
        if (!changed) return;

        applyDoubleJumpToPlayer(uuid, newState);
        requestPersistence(data);
    }

    private void requestPersistence(PlayerData data) {
        if (!plugin.getDatabaseManager().isActive()) return;
        saveQueue.queueSave(data);
    }

    private void applyDoubleJumpToPlayer(UUID uuid, NBTKeys newState) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) return;

        player.getPersistentDataContainer()
                .set(doubleJumpKey, PersistentDataType.STRING, newState.get());
    }

    /**
     * Apply visibility NBT to player's PDC
     */
    private void updateVisibility(Player player, boolean vis) {

        PlayerData current = this.dataMap.get(player.getUniqueId());
        current.setHideMode(vis);

        this.dataMap.replace(player.getUniqueId(), current);
    }

    /**
     * Load player data from DB and add to cache
     * Called on AsyncPlayerPreLoginEvent
     */
    public void addPlayer(Player player) {
        UUID uuid = player.getUniqueId();

        // Load from DB async (already async event)
        try {
            PlayerData data = loadFromDB(uuid, player.getName());
            if (data == null) {
                // New player - create default data
                data = new PlayerData(uuid, player.getName(), null, false);
            }
            dataMap.put(uuid, data);
            plugin.getLogger().fine("Loaded data for " + player.getName());
        } catch (SQLException e) {
            plugin.getLogger().warning("Failed to load data for " + player.getName());
            e.printStackTrace();
            // Create default data on failure
            dataMap.put(uuid, new PlayerData(uuid, player.getName(), null, false));
        }
    }

    /**
     * Load player data from DB and add to cache.
     * Called on AsyncPlayerPreLoginEvent.
     */
    public void loadOrCreate(UUID uuid, String name) {
        try {
            PlayerData data = loadFromDB(uuid, name);

            if (data == null) {
                data = new PlayerData(uuid, name, null, false);
            }

            dataMap.put(uuid, data);

        } catch (SQLException e) {
            plugin.getLogger().warning("Failed to load data for " + name);
            e.printStackTrace();

            dataMap.put(uuid, new PlayerData(uuid, name, null, false));
        }
    }

    /**
     * Apply cached data to player's PDC
     * Called on PlayerJoinEvent
     */
    public void applyToPlayer(Player player) {
        PlayerData data = dataMap.get(player.getUniqueId());
        if (data == null) {
            plugin.getLogger().warning("No data found for " + player.getName() + " on join!");
            return;
        }

        PersistentDataContainer container = player.getPersistentDataContainer();

        // Apply double jump
        if (data.getDoubleJumpNbt() != null) {
            container.set(doubleJumpKey, PersistentDataType.STRING, data.getDoubleJumpNbt());
        }

        if(data.getHideMode()) {

        }
    }

    /**
     * Save player to DB (force immediate save)
     * Called on PlayerQuitEvent
     */
    public void savePlayer(Player player) {
        UUID uuid = player.getUniqueId();
        PlayerData data = dataMap.get(uuid);

        if (data == null) {
            plugin.getLogger().warning("No data to save for " + player.getName());
            return;
        }

        // Extract latest data from PDC before saving
        PersistentDataContainer container = player.getPersistentDataContainer();
        String doubleJump = container.get(doubleJumpKey, PersistentDataType.STRING);
        boolean visibility = plugin.getManagerFactory().getPlayerVisibilityManager().isHideMode(player);

        data.setDoubleJumpNbt(doubleJump);
        data.setHideMode(visibility);

        // Force immediate save (blocking)
        saveQueue.saveNow(data);
    }

    /**
     * Remove player from cache after saving
     * Called on PlayerQuitEvent
     */
    public void removePlayer(Player player) {
        dataMap.remove(player.getUniqueId());
    }

    /**
     * Called when data changes - queue async save
     */
    private void onChange(PlayerData data) {
        saveQueue.queueSave(data);
        plugin.getDebugMode().info("Queued save for " + data.getName());
    }

    /**
     * Load player data from database
     */
    private PlayerData loadFromDB(UUID uuid, String name) throws SQLException {
        if (!plugin.getDatabaseManager().isActive()) {
            return null;
        }

        try (Connection conn = plugin.getDatabaseManager().getDatabase().getConnection()) {
            String sql = "SELECT name, double_jump_nbt, hide_mode FROM player_data WHERE uuid = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, uuid.toString());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new PlayerData(
                        uuid,
                        rs.getString("name"),
                        rs.getString("hide_mode"),
                        rs.getBoolean("player_visibility_nbt")
                );
            }
            return null; // Player not found
        }
    }

    /**
     * Save player data to database (called by AsyncSaveQueue)
     */
    public void saveToDB(PlayerData data) throws SQLException {
        if (!plugin.getDatabaseManager().isActive()) {
            return;
        }

        try (Connection conn = plugin.getDatabaseManager().getDatabase().getConnection()) {
            String sql = "INSERT INTO player_data (uuid, name, double_jump_nbt, hide_mode, last_seen) " +
                    "VALUES (?, ?, ?, ?, NOW()) " +
                    "ON DUPLICATE KEY UPDATE " +
                    "name = ?, double_jump_nbt = ?, hide_mode = ?, last_seen = NOW()";

            PreparedStatement stmt = conn.prepareStatement(sql);
            // Insert values
            stmt.setString(1, data.getUuid().toString());
            stmt.setString(2, data.getName());
            stmt.setString(3, data.getDoubleJumpNbt());
            stmt.setBoolean(4, data.getHideMode());
            // Update values (for ON DUPLICATE KEY)
            stmt.setString(5, data.getName());
            stmt.setString(6, data.getDoubleJumpNbt());
            stmt.setBoolean(7, data.getHideMode());

            stmt.executeUpdate();
        }
    }

    /**
     * Save all players (on shutdown)
     */
    public void saveAll() {
        plugin.getLogger().info("Saving all player data...");
        for (PlayerData data : dataMap.values()) {
            saveQueue.saveNow(data);
        }
    }

    /**
     * Shutdown the save queue
     */
    public void shutdown() {
        saveQueue.shutdown();
    }

    /**
     * Get cache size (for debugging)
     */
    public int getCacheSize() {
        return dataMap.size();
    }
}