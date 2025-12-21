package me.calrl.hubbly.listeners.player;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.managers.PlayerDataManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

public class AsyncPreLoginListener implements Listener {
    private final Hubbly plugin;
    public AsyncPreLoginListener(Hubbly plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    private void onPreLogin(AsyncPlayerPreLoginEvent event) {
        UUID uuid = event.getUniqueId();
        PlayerDataManager repository = plugin.getManagerFactory().getRepository();
        repository.loadOrCreate(uuid, event.getName());
    }

    @EventHandler
    private void onLogin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerDataManager repository = plugin.getManagerFactory().getRepository();
        repository.applyToPlayer(player);
    }
}
