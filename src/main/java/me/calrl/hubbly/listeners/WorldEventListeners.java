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
package me.calrl.hubbly.listeners;

import me.calrl.hubbly.Hubbly;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructure;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

import java.util.EnumSet;
import java.util.Set;
import java.util.logging.Logger;

public class WorldEventListeners implements Listener {

    private final Logger logger;
    private FileConfiguration config = Hubbly.getInstance().getConfig();
    public WorldEventListeners(Logger logger) {
        this.logger = logger;
    }
    private static final Set<EntityType> HOSTILE_MOBS = EnumSet.of(
            EntityType.ZOMBIE, EntityType.SKELETON, EntityType.CREEPER,
            EntityType.SPIDER, EntityType.ENDERMAN, EntityType.WITCH,
            EntityType.WITHER, EntityType.BLAZE, EntityType.GHAST
            // Add other hostile mobs as needed
    );

    private static final Set<EntityType> FRIENDLY_MOBS = EnumSet.of(
            EntityType.COW, EntityType.SHEEP, EntityType.PIG,
            EntityType.CHICKEN, EntityType.HORSE, EntityType.VILLAGER
            // Add other friendly mobs as needed
    );

    @EventHandler
    private void onBlockPlace(BlockPlaceEvent event) {
        if(config.getBoolean("cancel_events.block_place")) {
            Player player = event.getPlayer();
            if(!player.hasPermission("hubbly.bypass") || !player.isOp()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    private void onBlockBreak(BlockBreakEvent event) {
        if (config.getBoolean("cancel_events.block_break")) {
            Player player = event.getPlayer();
            if (!player.hasPermission("hubbly.bypass") || !player.isOp()) {
                event.setCancelled(true);
            }
        }
    }
    @EventHandler
    private void cancelDamage(EntityDamageEvent event) {
        if(config.getBoolean("cancel_events.damage")) {
            if(event.getEntity() instanceof Player player) {
                if (!player.hasPermission("hubbly.bypass") || !player.isOp()) {
                    event.setCancelled(true);
                }
            }
        }
    }
    @EventHandler
    private void onWeatherChange(WeatherChangeEvent event) {
        if(config.getBoolean("cancel_events.weather")) {
            event.setCancelled(true);
        }
    }
    @EventHandler
    private void onItemDrop(PlayerDropItemEvent event) {
        if(config.getBoolean("cancel_events.item_drop")) {
            Player player = event.getPlayer();
            if (!player.hasPermission("hubbly.bypass") || !player.isOp()) {
                event.setCancelled(true);
            }
        }
    }
    @EventHandler
    private void onItemPickup(EntityPickupItemEvent event) {
        if(config.getBoolean("cancel_events.item_pickup")) {
            if(event.getEntity() instanceof Player player) {
                if (!player.hasPermission("hubbly.bypass") || !player.isOp()) {
                    event.setCancelled(true);
                }
            }
        }
    }
    @EventHandler
    private void onFoodLevelChange(FoodLevelChangeEvent event) {
        if(config.getBoolean("cancel_events.hunger")) {
            if(event.getEntity() instanceof Player player) {
                if (!player.hasPermission("hubbly.bypass") || !player.isOp()) {
                    event.setCancelled(true);
                }
            }
        }
    }
    @EventHandler
    private void onMobSpawn(CreatureSpawnEvent event) {
        if (config.getBoolean("cancel_events.mob_spawn")) {
            EntityType entityType = event.getEntityType();
            if(!(event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.COMMAND || event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.CUSTOM)) {
                    event.setCancelled(true);
            }
        }
    }
    @EventHandler
    private void onBlockBurn(BlockBurnEvent event) {
        if(config.getBoolean("cancel_events.block_burn")) {
            event.setCancelled(true);
        }
    }
    @EventHandler
    private void onPlayerDeath(PlayerDeathEvent event) {
        if(config.getBoolean("cancel_events.death_messages")) {
            event.setDeathMessage("");
        }
    }
    @EventHandler
    private void onLeafDecay(LeavesDecayEvent event) {
        if(config.getBoolean("cancel_events.leaf_decay")) {
            event.setCancelled(true);
        }
    }
}
