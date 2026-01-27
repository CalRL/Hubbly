package me.calrl.hubbly.managers;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.enums.PluginKeys;
import me.calrl.hubbly.enums.Result;
import me.calrl.hubbly.enums.data.PlayerVisibilityMode;
import me.calrl.hubbly.storage.PlayerData;
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
        return player.getPersistentDataContainer().has(hideKey, PersistentDataType.BYTE);
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
        container.set(this.hideKey, PersistentDataType.STRING, mode.toString());

        FileConfiguration config = this.plugin.getConfig();
        StorageManager storage = this.plugin.getStorageManager();
        if(config.getBoolean("database.enabled") && storage.isActive()) {
            PlayerData data = PlayerData.from(player);
            storage.enqueueSave(data);
        }
    }

    public void setHideMode(Player player, boolean hidden) {
        if (hidden) {
            player.getPersistentDataContainer().set(hideKey, PersistentDataType.BYTE, (byte) 1);
            if(player.getPersistentDataContainer().has(hideKey)) {
                this.hideAll(player);
            } else {
                new DebugMode(this.plugin).info(String.format("Failed to enable hide mode for player: %s", player));
            }
        } else {
            player.getPersistentDataContainer().remove(hideKey);
            if(!player.getPersistentDataContainer().has(hideKey)) {
                this.revealAll(player);
            } else {
                new DebugMode(this.plugin).info(String.format("Failed to disable hide mode for player: %s", player));
            }
        }
    }

    public void handleJoin(Player joined) {
        for (Player viewer : Bukkit.getOnlinePlayers()) {
            if (isHideMode(viewer)) {
                new DebugMode(plugin).info(String.format("Player: %s is in hide mode?", viewer));
                viewer.hidePlayer(plugin, joined);
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
