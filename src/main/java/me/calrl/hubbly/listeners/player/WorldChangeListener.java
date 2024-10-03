package me.calrl.hubbly.listeners.player;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.functions.BossBarManager;
import me.calrl.hubbly.managers.AnnouncementsManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public class WorldChangeListener implements Listener {

    private Hubbly plugin;
    public WorldChangeListener(Hubbly plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    private void onWorldChange(PlayerChangedWorldEvent event) {

        Player player = event.getPlayer();
        plugin.getDebugMode().info(player.getName() + " switched worlds: " + player.getWorld());

        BossBarManager bossBarManager = BossBarManager.getInstance();
        if(plugin.getDisabledWorldsManager().inDisabledWorld(player.getWorld())) {
            bossBarManager.removeBossBar(player);
            return;
        };

        player.setHealth(20);

        if (plugin.getConfig().getBoolean("cancel_events.hunger")) {
            player.setFoodLevel(20);
        }

        if(!bossBarManager.hasBossBar(player)) {
            bossBarManager.createBossBar(player);
        }
    }

}