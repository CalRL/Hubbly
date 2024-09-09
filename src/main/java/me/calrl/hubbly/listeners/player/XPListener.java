package me.calrl.hubbly.listeners.player;

import me.calrl.hubbly.Hubbly;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class XPListener implements Listener {

    private Hubbly plugin;
    public XPListener(Hubbly plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        player.setLevel((int) plugin.getConfig().getDouble("player.experience.level"));
        player.setExp(1);

    }

    @EventHandler
    private void onPlayerXP(PlayerExpChangeEvent event) {
        if(plugin.getConfig().getBoolean("player.experience.enabled")) {
            event.setAmount(0);
        }

    }
}
