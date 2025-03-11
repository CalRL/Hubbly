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
package me.calrl.hubbly.listeners.items;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.enums.Permissions;
import me.calrl.hubbly.enums.PluginKeys;
import me.calrl.hubbly.enums.TridentSounds;
import me.calrl.hubbly.managers.cooldown.CooldownType;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import java.util.Random;


public class MovementItemListener implements Listener {

    private final Hubbly plugin;
    private FileConfiguration config;
    public MovementItemListener(Hubbly plugin) {
        this.plugin = plugin;
        config = plugin.getConfig();
    }

    @EventHandler
    private void onBowShoot(EntityShootBowEvent event) {
        if(!config.getBoolean("movementitems.enderbow.enabled")) return;
        if(!(event.getBow().getItemMeta().getPersistentDataContainer().has(PluginKeys.ENDER_BOW.getKey()))) return;
        if(event.getEntity() instanceof Player player) {

            if(!player.hasPermission(Permissions.USE_ENDER_BOW.getPermission())) {
                player.sendMessage(String.valueOf(player.hasPermission(Permissions.USE_ENDER_BOW.getPermission())));
                return;
            }

            if(!plugin.getCooldownManager().tryCooldown(player.getUniqueId(), CooldownType.ENDER_BOW, config.getLong("movementitems.enderbow.cooldown"))) {
                event.setCancelled(true);
                player.getInventory().setItem(17, new ItemStack(Material.ARROW));
                return;
            }

            if(!(event.getProjectile() instanceof Arrow arrow)) return;
            PersistentDataContainer container = arrow.getPersistentDataContainer();
            container.set(PluginKeys.ENDER_BOW.getKey(), PersistentDataType.STRING, "arrow");

            player.getInventory().setItem(17, new ItemStack(Material.ARROW));
        }
    }
    @EventHandler
    private void onArrowLand(ProjectileHitEvent event) {
        if(!config.getBoolean("movementitems.enderbow.enabled")) return;
        if(!(event.getEntity().getShooter() instanceof Player player)) return;
        if(event.getEntity() instanceof Arrow arrow) {
            PersistentDataContainer container = arrow.getPersistentDataContainer();
            if(container != null && container.has(PluginKeys.ENDER_BOW.getKey())) {
                Location location = arrow.getLocation();
                location.setPitch(player.getLocation().getPitch());
                location.setYaw(player.getLocation().getYaw());
                arrow.remove();
                player.teleport(location);
                player.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
            }
        }
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event) {
        if(!config.getBoolean("movementitems.enderbow.enabled")) return;

        final Player player = event.getPlayer();
        if(!player.hasPermission(Permissions.USE_ENDER_BOW.getPermission())) {
            return;
        }

        player.getInventory().setItem(17, new ItemStack(Material.ARROW));
    }

    @EventHandler
    public void onTridentThrow(ProjectileLaunchEvent event) {
        if (event.getEntity() instanceof Trident trident && trident.getShooter() instanceof Player player) {
            if(!player.hasPermission(Permissions.USE_TRIDENT.getPermission())) return;
            ItemStack itemInHand = player.getInventory().getItemInMainHand();
            ItemMeta meta = itemInHand.getItemMeta();

            if(meta != null && itemInHand.getType() == Material.TRIDENT && itemInHand.getItemMeta().getPersistentDataContainer().has(PluginKeys.TRIDENT.getKey())) {
                if(!plugin.getCooldownManager().tryCooldown(player.getUniqueId(), CooldownType.TRIDENT, config.getLong("movementitems.trident.cooldown"))) {
                    event.setCancelled(true);
                    return;
                }

                event.getEntity().getPersistentDataContainer().set(PluginKeys.TRIDENT.getKey(), PersistentDataType.STRING, "trident");
            }

            if(player.getGameMode() == GameMode.ADVENTURE || player.getGameMode() == GameMode.SURVIVAL) {
                ItemStack newTrident = itemInHand.clone();
                player.getInventory().addItem(newTrident);
            }

        }
    }

    @EventHandler
    private void onTridentLand(ProjectileHitEvent event) {
        if (event.getEntity() instanceof Trident trident && event.getEntity().getShooter() instanceof Player player) {
            config = plugin.getConfig();
            if(!config.getBoolean("movementitems.trident.enabled")) return;


            if (trident.getPersistentDataContainer().has(PluginKeys.TRIDENT.getKey())) {
                trident.remove();

                player.teleport(trident.getLocation().setDirection(player.getLocation().getDirection()));
                player.playSound(player.getLocation(), randomSound().getSound(), 1.0F, 1.0F);
            }
        }
    }
    private TridentSounds randomSound() {
        int pick = new Random().nextInt(TridentSounds.values().length);
        return TridentSounds.values()[pick];
    }

    @EventHandler
    private void onFishingRodUse(PlayerFishEvent event) {
        if (!config.getBoolean("movementitems.grappling_hook.enabled")) return;

        Player player = event.getPlayer();
        if(!player.hasPermission(Permissions.USE_GRAPPLING_HOOK.getPermission())) return;
        ItemMeta meta = player.getInventory().getItemInMainHand().getItemMeta();

        if (event.getState() != PlayerFishEvent.State.FAILED_ATTEMPT
                && event.getState() != PlayerFishEvent.State.IN_GROUND
                && event.getState() != PlayerFishEvent.State.REEL_IN) return;

        if (meta != null && meta.getPersistentDataContainer().has(PluginKeys.GRAPPLING_HOOK.getKey())) {
            if(!plugin.getCooldownManager().tryCooldown(player.getUniqueId(), CooldownType.GRAPPLING_HOOK, config.getLong("movementitems.grappling_hook.cooldown"))) return;

            Location hookLocation = event.getHook().getLocation();
            Location playerLocation = player.getLocation();

            double distance = hookLocation.distance(playerLocation);
            Vector direction = hookLocation.toVector().subtract(playerLocation.toVector()).normalize();

            Vector velocity = direction.multiply(distance / 3.5);
            player.setVelocity(velocity);
        }
    }

    @EventHandler
    private void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        ItemMeta meta = item.getItemMeta();
        if(meta != null && meta.getPersistentDataContainer().has(PluginKeys.AOTE.getKey())) {
            if(!plugin.getCooldownManager().tryCooldown(player.getUniqueId(), CooldownType.AOTE, config.getLong("movementitems.aote.cooldown"))) return;
            if(!player.hasPermission(Permissions.USE_AOTE.getPermission())) return;
            player.teleport(getLocationInFront(player, 10));

        }
    }

    private Location getLocationInFront(Player player, double distance) {
        Location currentLocation = player.getLocation();
        Vector direction = currentLocation.getDirection();
        Vector frontVector = direction.normalize().multiply(distance);
        Location targetLocation = currentLocation.add(frontVector);

        return targetLocation;
    }

}
