package me.calrl.hubbly.managers;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.enums.PluginKeys;
import me.calrl.hubbly.enums.Result;
import me.calrl.hubbly.enums.data.PlayerVisibilityMode;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PlayerVisibilityManager{
    private final Set<UUID> players = new HashSet<>();
    private final NamespacedKey hideKey;
    private final Hubbly plugin;

    public PlayerVisibilityManager(Hubbly plugin) {
        this.plugin = plugin;
        this.hideKey = PluginKeys.PLAYER_VISIBILITY.getKey();
    }

    public void reload() {
        this.plugin.getServer().getOnlinePlayers().forEach(this::applyHideState);
    }

    private void applyHideState(Player player) {
        if (isHideMode(player)) {
            for (Player other : this.plugin.getServer().getOnlinePlayers()) {
                player.hidePlayer(this.plugin, other);
            }
        }
    }

    public boolean isHideMode(Player player) {
        PersistentDataContainer container = player.getPersistentDataContainer();
        if (container.has(hideKey, PersistentDataType.BYTE)) {
            migrateLegacyHideMode(player);
        }
        String value = container.get(hideKey, PersistentDataType.STRING);
        return value != null && value.equalsIgnoreCase(PlayerVisibilityMode.HIDDEN.name());
    }

    public void sendMessageToVisiblePlayers(String message) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!isHideMode(player)) {
                player.sendMessage(message);
            }
        }
    }

    private void hideAll(Player player) {
        for(Player onlinePlayer : this.plugin.getServer().getOnlinePlayers()) {
            player.hidePlayer(this.plugin, onlinePlayer);
        }
    }

    private void revealAll(Player player) {
        for(Player onlinePlayer : this.plugin.getServer().getOnlinePlayers()) {
            player.showPlayer(this.plugin, onlinePlayer);
        }
    }

    public void setHideMode(Player player, PlayerVisibilityMode mode) {
        PersistentDataContainer container = player.getPersistentDataContainer();
        container.set(this.hideKey, PersistentDataType.STRING, mode.name());

        if (mode == PlayerVisibilityMode.HIDDEN) {
            this.hideAll(player);
        } else {
            this.revealAll(player);
        }

        FileConfiguration config = this.plugin.getConfig();
        StorageManager storage = this.plugin.getStorageManager();
        if(config.getBoolean("database.enabled") && storage.isActive()) {
            storage.updateVisibilityMode(player, mode);
        }
    }

    /**
     * @deprecated Use {@link #setHideMode(Player, PlayerVisibilityMode)} to persist STRING values.
     */
    @Deprecated
    public void setHideMode(Player player, boolean hidden) {
        this.setHideMode(player, hidden ? PlayerVisibilityMode.HIDDEN : PlayerVisibilityMode.VISIBLE);
    }

    private void migrateLegacyHideMode(Player player) {
        PersistentDataContainer container = player.getPersistentDataContainer();
        Byte legacyValue = container.get(hideKey, PersistentDataType.BYTE);
        container.remove(hideKey);
        if (legacyValue != null && legacyValue == (byte) 1) {
            this.setHideMode(player, PlayerVisibilityMode.HIDDEN);
        } else {
            this.setHideMode(player, PlayerVisibilityMode.VISIBLE);
        }
    }

    public void handleJoin(Player joined) {
        boolean joinedHidden = isHideMode(joined);

        for (Player viewer : Bukkit.getOnlinePlayers()) {
            if(viewer.equals(joined)) continue;

            if (isHideMode(viewer)) {
                viewer.hidePlayer(plugin, joined);
            }
            if (joinedHidden) {
                joined.hidePlayer(plugin, viewer);
            }
        }
    }

    private NamespacedKey getKey() {
        return this.hideKey;
    }

    public Set<UUID> getHideModePlayers() {
        return this.players;
    }

    public Result add(Player player) {
        return this.add(player.getUniqueId());
    }

    private Result add(UUID uuid) {
        boolean success = this.players.add(uuid);
        return Result.from(success);
    }

    private Result remove(UUID uuid) {
        return Result.from(this.players.remove(uuid));
    }

    public boolean contains(UUID uuid) {
        return this.players.contains(uuid);
    }
}
