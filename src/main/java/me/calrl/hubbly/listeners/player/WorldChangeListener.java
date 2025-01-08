package me.calrl.hubbly.listeners.player;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.functions.BossBarManager;
import me.calrl.hubbly.managers.DisabledWorlds;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
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
        World world = player.getWorld();

        FileConfiguration config = plugin.getConfig();
        DisabledWorlds disabledWorlds = plugin.getDisabledWorldsManager();

        plugin.getDebugMode().info(player.getName() + " switched worlds: " + player.getWorld());

        BossBarManager bossBarManager = plugin.getBossBarManager();
        if(disabledWorlds.inDisabledWorld(player.getWorld())) {
            bossBarManager.removeBossBar(player);
        }

        player.setHealth(20);

        if (config.getBoolean("cancel_events.hunger")) {
            player.setFoodLevel(20);
        }

        //TODO: put this in the playerManager class.
        if(disabledWorlds.inDisabledWorld(world)) {
            player.setAllowFlight(false);
            player.setFlying(false);
        } else {
            player.setAllowFlight(true);
        }

        if(!bossBarManager.hasBossBar(player)) {
            bossBarManager.createBossBar(player);
        }
    }

}