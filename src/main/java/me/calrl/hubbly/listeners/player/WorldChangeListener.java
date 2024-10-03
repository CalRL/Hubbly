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
        if(plugin.getDisabledWorldsManager().inDisabledWorld(player.getWorld())) return;

        BossBarManager bossBarManager = BossBarManager.getInstance();

        if(!bossBarManager.hasBossBar(player)) {
            bossBarManager.createBossBar(player);
        }
    }

}
