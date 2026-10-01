package me.calrl.hubbly.storage;

import org.bukkit.entity.Player;

import java.util.UUID;

public interface PlayerDataStore extends AutoCloseable {
    void preload(UUID uuid);
    PlayerData get(Player player);
    void save(PlayerData data);
    void flush();
}
