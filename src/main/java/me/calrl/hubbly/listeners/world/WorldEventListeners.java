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
import me.calrl.hubbly.managers.DisabledWorlds;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.projectiles.ProjectileSource;

public class WorldEventListeners implements Listener {

    private FileConfiguration config;
    private final Hubbly plugin;
    
    private final BossBarManager bossBarManager;

    public WorldEventListeners(Hubbly plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        this.bossBarManager = plugin.getBossBarManager();
        this.plugin.getLogger().info("Loaded WorldEventListeners");
    }

    /**
     * Check if player is in disabled world.
     *
     * @param player the player
     * @return return true if the player is in a disabled world
     */
    private boolean inDisabledWorld(Player player) {
        DisabledWorlds disabledWorlds = plugin.getDisabledWorldsManager();
        return disabledWorlds.inDisabledWorld(player.getLocation());
    }

    /**
     * Check if world is a disabled world
     *
     * @param world thr world
     * @return return true if the world is a disabled world
     */
    private boolean inDisabledWorld(World world) {
        DisabledWorlds disabledWorlds = plugin.getDisabledWorldsManager();
        return disabledWorlds.inDisabledWorld(world);
    }


    @EventHandler
    private void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if(inDisabledWorld(player)) return;
        if(player.hasPermission("hubbly.bypass.place") ) return;
        if(config.getBoolean("cancel_events.block_place")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if(inDisabledWorld(player)) return;
        if(player.hasPermission("hubbly.bypass.break") ) return;
        if (config.getBoolean("cancel_events.block_break")) {

                event.setCancelled(true);

        }
    }
    @EventHandler
    private void cancelDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (player.hasPermission("hubbly.bypass.damage")) return;
            if (inDisabledWorld(player)) return;
            if(config.getBoolean("cancel_events.damage")) {
                event.setCancelled(true);
            }
        }
    }
    @EventHandler
    private void onWeatherChange(WeatherChangeEvent event) {
        if(inDisabledWorld(event.getWorld())) return;
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
        if(inDisabledWorld(player)) return;
        if (player.hasPermission("hubbly.bypass.item.drop") ) return;
        if(config.getBoolean("cancel_events.item_drop")) {
                event.setCancelled(true);
        }
    }
    @EventHandler
    private void onItemPickup(EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (inDisabledWorld(player)) return;
            if (player.hasPermission("hubbly.bypass.item.pickup") ) return;
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
                if (inDisabledWorld(player)) return;

                boolean hasKey = doesPlayerHaveItemWithKey(player);
                if(!hasKey) {
                    event.setCancelled(true);
                }

            } else {
                event.setCancelled(true);
            }
        }
    }

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
    private void onAnimalEat(EntityChangeBlockEvent event) {
        EntityType type = event.getEntityType();
        Block block = event.getBlock();
        if(inDisabledWorld(block.getWorld()))
        if(type != EntityType.SHEEP) return;
        if(event.getTo() == Material.DIRT) {
            block.setType(Material.DIRT);
        }
    }


    @EventHandler
    private void onItemThrow(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if(config.getBoolean("cancel_events.item_throw", true)) {
            if (player.hasPermission("hubbly.bypass.item.throw") ) return;
            if (inDisabledWorld(player)) return;

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



    @EventHandler
    private void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (inDisabledWorld(player)) return;
            if (player.hasPermission("hubbly.bypass.food")) return;
            if (config.getBoolean("cancel_events.hunger")) {
                event.setCancelled(true);
            }
        }
    }
    @EventHandler
    private void onMobSpawn(CreatureSpawnEvent event) {
        if(inDisabledWorld(event.getLocation().getWorld())) return;
        if (config.getBoolean("cancel_events.mob_spawn")) {
            if(!(event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.COMMAND || event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.CUSTOM)) {
                event.setCancelled(true);
            }
        }
    }
    @EventHandler
    private void onBlockBurn(BlockBurnEvent event) {
        if(inDisabledWorld(event.getBlock().getWorld())) return;
        if(config.getBoolean("cancel_events.block_burn")) {
            event.setCancelled(true);
        }
    }
    @EventHandler
    private void onBlockIgnite(BlockIgniteEvent event) {
        if(inDisabledWorld(event.getBlock().getWorld())) return;
        if(config.getBoolean("cancel_events.block_burn")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onBlockSpread(BlockSpreadEvent event) {
        if(inDisabledWorld(event.getBlock().getWorld())) return;
        if(config.getBoolean("cancel_events.block_burn")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        if(inDisabledWorld(player.getWorld())) return;
        if(config.getBoolean("cancel_events.death_messages")) {
            event.setDeathMessage("");
        }

    }
    @EventHandler
    private void onLeafDecay(LeavesDecayEvent event) {
        if(inDisabledWorld(event.getBlock().getWorld())) return;
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

        Location location;
        // Fix for paper, paper doesn't have Player#getRespawnLocation ..?
        try {
            location = event.getRespawnLocation();
        } catch (NoSuchMethodError e) {
            location = null;
            plugin.getDebugMode().severe(e.getMessage());
        }

        Player player = event.getPlayer();
        if(location == null) {
            try {
                location = player.getRespawnLocation();
            } catch (NoSuchMethodError e) {
                plugin.getDebugMode().severe(e.getMessage());
            }
        }

        if(location == null) {
            plugin.getLogger().severe("Could not get respawn location.");
            plugin.getLogger().severe("Please report this to the developer.");
            return;
        }

        if(inDisabledWorld(location.getWorld())) {
            bossBarManager.removeBossBar(player);
        }
    }

    /**
     * Cancel Interact events where necessary.
     *
     * @param event
     */
    @EventHandler
    private void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if(player.hasPermission(Permissions.BYPASS_INTERACT.getPermission())) return;
        if(inDisabledWorld(player.getWorld())) return;
        if(config.getBoolean("cancel_events.interact", false)) {
            event.setCancelled(true);
        }
    }

}
