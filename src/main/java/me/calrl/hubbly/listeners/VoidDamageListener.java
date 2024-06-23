package me.calrl.hubbly.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.Objects;
import java.util.logging.Logger;

public class VoidDamageListener implements Listener {

    private final FileConfiguration config;
    private final Logger logger;
    public VoidDamageListener(Logger logger, FileConfiguration config) {
        this.logger = logger;
        this.config = config;
    }

    @EventHandler
    private void onEntityDamage(EntityDamageEvent event) {
        if(event.getEntity() instanceof Player && config.getBoolean("antivoid.enabled")) {
            if(event.getCause() == EntityDamageEvent.DamageCause.VOID) {
                event.setCancelled(true);
                String worldName = config.getString("spawn.world");
                World world = Bukkit.getWorld(worldName);
                double x = config.getDouble("spawn.x");
                double y = config.getDouble("spawn.y");
                double z = config.getDouble("spawn.z");
                float yaw = (float) config.getDouble("spawn.yaw");
                float pitch = (float) config.getDouble("spawn.pitch");
                event.getEntity().teleport(new Location(world, x, y, z, yaw, pitch));

            }
        }
    }
}
