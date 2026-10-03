package me.calrl.hubbly.storage.pdc;

import me.calrl.hubbly.storage.PlayerData;
import me.calrl.hubbly.storage.PlayerDataStore;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

public class PDCPlayerStore implements PlayerDataStore {
    /**
     * Preloads player data for use during {@link PlayerJoinEvent}.
     *
     * <p>This method intentionally returns no value because it is called from
     * {@link AsyncPlayerPreLoginEvent}; the preloaded data is stored internally
     * and consumed when the player joins.</p>
     */
    @Override
    public void preload(UUID uuid) {}

    @Override
    public PlayerData get(Player player) {
        return PlayerData.from(player);
    }

    @Override
    public void save(PlayerData data) {}

    @Override
    public void flush() {}

    @Override
    public void close() throws Exception {}
}
