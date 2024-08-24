/*
 * This file is part of Hubbly.
 *
 * Hubbly is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Hubbly is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Hubbly. If not, see <http://www.gnu.org/licenses/>.
 */
package me.calrl.hubbly.listeners.world;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.managers.DebugMode;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.projectiles.ProjectileSource;

import java.util.logging.Logger;

public class WorldEventListeners implements Listener {

    private final Logger logger;
    private FileConfiguration config = Hubbly.getInstance().getConfig();
    private final Hubbly plugin;
    private final DebugMode debugMode;
    public WorldEventListeners(Hubbly plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.debugMode = plugin.getDebugMode();

    }
    private boolean checkWorld(Player player) {
        return plugin.getDisabledWorldsManager().inDisabledWorld(player.getLocation());
    }


    @EventHandler
    private void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if(checkWorld(player)) return;
        if(player.hasPermission("hubbly.bypass.place") || player.isOp()) return;
        if(config.getBoolean("cancel_events.block_place")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if(checkWorld(player)) return;
        if(player.hasPermission("hubbly.bypass.break") || player.isOp()) return;
        if (config.getBoolean("cancel_events.block_break")) {

                event.setCancelled(true);

        }
    }
    @EventHandler
    private void cancelDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (player.hasPermission("hubbly.bypass.damage") || player.isOp()) return;
            if (checkWorld(player)) return;
            if(config.getBoolean("cancel_events.damage")) {
                event.setCancelled(true);
            }
        }
    }
    @EventHandler
    private void onWeatherChange(WeatherChangeEvent event) {
        if(Hubbly.getInstance().getDisabledWorldsManager().inDisabledWorld(event.getWorld())) return;
        if(config.getBoolean("cancel_events.weather")) {
            event.setCancelled(true);
        }
    }
    @EventHandler
    private void onItemDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if(checkWorld(player)) return;
        if (player.hasPermission("hubbly.bypass.item.drop") || player.isOp()) return;
        if(config.getBoolean("cancel_events.item_drop")) {
                event.setCancelled(true);
        }
    }
    @EventHandler
    private void onItemPickup(EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (checkWorld(player)) return;
            if (player.hasPermission("hubbly.bypass.item.pickup") || player.isOp()) return;
            if (config.getBoolean("cancel_events.item_pickup")) {
                event.setCancelled(true);
            }
        }
    }
    @EventHandler
    private void onItemThrow(ProjectileLaunchEvent event) {
        ProjectileSource source = event.getEntity().getShooter();
        if(config.getBoolean("cancel_events.item_throw", true)) {
            if (source instanceof Player player) {
                if (player.hasPermission("hubbly.bypass.item.throw") || player.isOp()) return;
                if (checkWorld(player)) return;
                event.setCancelled(true);
            } else {
                event.setCancelled(true);
            }
        }
    }
    @EventHandler
    private void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (checkWorld(player)) return;
            if (player.hasPermission("hubbly.bypass.food") || player.isOp()) return;
            if (config.getBoolean("cancel_events.hunger")) {
                event.setCancelled(true);
            }
        }
    }
    @EventHandler
    private void onMobSpawn(CreatureSpawnEvent event) {
        if(Hubbly.getInstance().getDisabledWorldsManager().inDisabledWorld(event.getLocation().getWorld())) return;
        if (config.getBoolean("cancel_events.mob_spawn")) {
            if(!(event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.COMMAND || event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.CUSTOM)) {
                event.setCancelled(true);
            }
        }
    }
    @EventHandler
    private void onBlockBurn(BlockBurnEvent event) {
        if(Hubbly.getInstance().getDisabledWorldsManager().inDisabledWorld(event.getBlock().getWorld())) return;
        if(config.getBoolean("cancel_events.block_burn")) {
            event.setCancelled(true);
        }
    }
    @EventHandler
    private void onBlockIgnite(BlockIgniteEvent event) {
        if(plugin.getDisabledWorldsManager().inDisabledWorld(event.getBlock().getWorld())) return;
        if(config.getBoolean("cancel_events.block_burn")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onBlockSpread(BlockSpreadEvent event) {
        if(plugin.getDisabledWorldsManager().inDisabledWorld(event.getBlock().getWorld())) return;
        if(config.getBoolean("cancel_events.block_burn")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onPlayerDeath(PlayerDeathEvent event) {
        if(plugin.getDisabledWorldsManager().inDisabledWorld(event.getEntity().getWorld())) return;
        if(config.getBoolean("cancel_events.death_messages")) {
            event.setDeathMessage("");
        }
    }
    @EventHandler
    private void onLeafDecay(LeavesDecayEvent event) {
        if(Hubbly.getInstance().getDisabledWorldsManager().inDisabledWorld(event.getBlock().getWorld())) return;
        if(config.getBoolean("cancel_events.leaf_decay")) {
            event.setCancelled(true);
        }
    }

}
