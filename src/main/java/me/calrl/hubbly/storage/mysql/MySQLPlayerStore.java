package me.calrl.hubbly.storage.mysql;

import me.calrl.hubbly.storage.PlayerData;
import me.calrl.hubbly.storage.PlayerDataStore;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class MySQLPlayerStore implements PlayerDataStore {
    private final HashMap<UUID, PlayerData> map;
    public MySQLPlayerStore() {
        this.map = new HashMap<>();
    }
    @Override
    public void preload(UUID uuid) {

    }

    @Override
    public PlayerData get(Player player) {
        return null;
    }

    @Override
    public void save(PlayerData data) {

    }

    @Override
    public void flush() {

    }

    @Override
    public void close() throws Exception {

    }
}
