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
import me.calrl.hubbly.enums.Permissions;
import me.calrl.hubbly.enums.PluginKeys;
import me.calrl.hubbly.functions.BossBarManager;
import me.calrl.hubbly.managers.DebugMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.projectiles.ProjectileSource;

import java.util.logging.Logger;

public class WorldEventListeners implements Listener {

    private final Logger logger;
    private FileConfiguration config = Hubbly.getInstance().getConfig();
    private final Hubbly plugin;
    private final DebugMode debugMode;

    // FIXME Fix the bossbar manager to look like the rest of the code, major refactor needed:
    // - Seriously, this looks bad

    private final BossBarManager bossBarManager = BossBarManager.getInstance();

    public WorldEventListeners(Hubbly plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.debugMode = plugin.getDebugMode();

    }

    /**
     * Check if player is in disabled world.
     *
     * @param player the player
     * @return return true if the player is in a disabled world
     */

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
            if (player.hasPermission("hubbly.bypass.damage")) return;
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
    private void onThunderChange(ThunderChangeEvent event) {
        if(config.getBoolean("cancel_events.weather")) {
            event.setCancelled(true);
        }
    }
    @EventHandler
    private void onWorldLoad(WorldLoadEvent event) {
        if(config.getBoolean("cancel_events.weather")) {
            event.getWorld().setThundering(false);
            event.getWorld().setStorm(false);
            event.getWorld().setClearWeatherDuration(200);
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
    private void onProjectile(ProjectileLaunchEvent event) {

        ProjectileSource source = event.getEntity().getShooter();

        if(config.getBoolean("cancel_events.projectiles", true)) {
            if (source instanceof Player player) {
                if (player.hasPermission(Permissions.BYPASS_PROJECTILES.getPermission())) return;
                if (checkWorld(player)) return;

                boolean hasKey = doesPlayerHaveItemWithKey(player);
                if(!hasKey) {
                    event.setCancelled(true);
                }

            } else {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    private void onAnimalEat(EntityChangeBlockEvent event) {
        EntityType type = event.getEntityType();
        if(type != EntityType.SHEEP) return;
        if(event.getTo() == Material.DIRT) {
            event.getBlock().setType(Material.DIRT);
        }
    }


    @EventHandler
    private void onItemThrow(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if(config.getBoolean("cancel_events.item_throw", true)) {
            if (player.hasPermission("hubbly.bypass.item.throw") || player.isOp()) return;
            if (checkWorld(player)) return;

        } else {
            event.setCancelled(true);
        }

    }
    private static final PluginKeys[] CHECK_KEYS = {
            PluginKeys.ENDER_BOW,
            PluginKeys.TRIDENT,
            PluginKeys.GRAPPLING_HOOK,
            PluginKeys.AOTE
    };

    public boolean doesPlayerHaveItemWithKey(Player player) {
        ItemStack item = player.getInventory().getItemInMainHand(); // Or offhand if you want to check both
        if (item == null || !item.hasItemMeta()) return false;

        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();

        // Loop through the subset of keys and check if the item has any of these keys
        for (PluginKeys key : CHECK_KEYS) {
            if (container.has(key.getKey(), PersistentDataType.STRING)) {
                return true;
            }
        }

        return false;
    }

    @EventHandler
    private void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (checkWorld(player)) return;
            if (player.hasPermission("hubbly.bypass.food")) return;
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
        Player player = event.getEntity();

        if(plugin.getDisabledWorldsManager().inDisabledWorld(player.getWorld())) return;
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

    /**
     * Extra check to make sure players don't respawn with bossbars if they respawn in a disabled world.
     *
     * @param event bukkit event
     */
    @EventHandler
    private void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        if(plugin.getDisabledWorldsManager().inDisabledWorld(player.getRespawnLocation())) {
            bossBarManager.removeBossBar(player);
            return;
        }
    }

}
