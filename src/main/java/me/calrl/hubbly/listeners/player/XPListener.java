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
        plugin.getLogger().info("set to x");
    }

    @EventHandler
    private void onPlayerXP(PlayerExpChangeEvent event) {
        if(plugin.getConfig().getBoolean("player.experience.enabled")) {
            event.setAmount(0);
            plugin.getLogger().info("set to 0");
        }

    }

    public double getRequiredExp(double level) {
        double e;

        if (level <= 16) {
           e = level * level + 6 * level;
        } else if (level <= 31) {
            e = (2.5 * level * level - 40.5 * level + 360);
        } else {
            e = (4.5 * level * level - 162.5 * level + 2220);
        }
        return e;
    }
}
