package me.calrl.hubbly.listeners.items.movement;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.enums.Permissions;
import me.calrl.hubbly.enums.PluginKeys;
import me.calrl.hubbly.managers.DisabledWorlds;
import me.calrl.hubbly.managers.cooldown.CooldownType;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

public class AoteListener implements Listener {
    private Hubbly plugin;
    public AoteListener(Hubbly plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    private void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getAction() != Action.RIGHT_CLICK_AIR
                && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        DisabledWorlds disabledWorlds = plugin.services().disabledWorlds();
        if (disabledWorlds.inDisabledWorld(player.getLocation())) return;

        ItemStack item = player.getInventory().getItemInMainHand();
        ItemMeta meta = item.getItemMeta();
        if (meta != null && meta.getPersistentDataContainer().has(PluginKeys.AOTE.getKey())) {

            event.setCancelled(true);
            event.setUseItemInHand(Event.Result.DENY);
            event.setUseInteractedBlock(Event.Result.DENY);

            FileConfiguration config = plugin.getConfig();
            if (!plugin.services().cooldowns().tryCooldown(player.getUniqueId(), CooldownType.AOTE, config.getLong("movementitems.aote.cooldown"))) return;
            if (!player.hasPermission(Permissions.USE_AOTE.getPermission())) return;

            final double distance = 6;
            Location destination = getTeleportDestination(player, distance);

            if (destination == null || !isSafe(destination)) {
                Location potential = getLocationAheadFlat(player, distance);

                if (!isSafe(potential)) {
                    return;
                }

                destination = potential;
            }

            destination.setYaw(player.getLocation().getYaw());
            destination.setPitch(player.getLocation().getPitch());
            player.teleport(destination);
        }
    }

    private Location getTeleportDestination(Player player, double distance) {
        Location eye = player.getEyeLocation();
        Vector dir = eye.getDirection().normalize();

        RayTraceResult hit = player.getWorld().rayTraceBlocks(
                eye,
                dir,
                distance,
                FluidCollisionMode.NEVER,
                true
        );

        if (hit == null) {
            return getLocationAhead(player, distance);
        }

        Block hitBlock = hit.getHitBlock();

        if (hit.getHitBlockFace() == BlockFace.UP || eye.getY() >= hitBlock.getY() + 1.0) {
            Location forward = getLocationAhead(player, distance);
            forward.setY(player.getLocation().getY());
            return findSafestLocationNear(forward);
        }

        // Side face (wall) - use the player's own current Y, not the hit's fractional Y
        Location inFrontOfWall = hit.getHitPosition().toLocation(player.getWorld())
                .subtract(dir.clone().multiply(0.6));
        inFrontOfWall.setY(player.getLocation().getY());

        return findSafestLocationNear(inFrontOfWall);
    }

    private Location findSafestLocationNear(Location reference) {
        int baseY = reference.getBlockY();
        int minY = reference.getWorld().getMinHeight();
        int maxY = reference.getWorld().getMaxHeight();

        final int searchRadius = 5;

        for (int offset = 0; offset <= searchRadius; offset++) {
            int upY = baseY + offset;
            int downY = baseY - offset;

            if (upY <= maxY) {
                Location candidate = reference.clone();
                candidate.setY(upY);
                if (isSafe(candidate)) return candidate;
            }

            if (offset != 0 && downY >= minY) {
                Location candidate = reference.clone();
                candidate.setY(downY);
                if (isSafe(candidate)) return candidate;
            }
        }

        return null;
    }

    private Location getLocationAhead(Player player, double distance) {
        Location eye = player.getEyeLocation();
        Vector dir = eye.getDirection().normalize();

        return player.getLocation().clone().add(dir.multiply(distance));
    }

    private Location getLocationAheadFlat(Player player, double distance) {
        Location eye = player.getEyeLocation();
        Vector dir = eye.getDirection().normalize();

        Location location = player.getLocation().clone().add(dir.multiply(distance));
        location.setY(player.getLocation().getY());
        return location;
    }

    private boolean isSafe(Location location) {
        int minY = location.getWorld().getMinHeight();
        int maxY = location.getWorld().getMaxHeight();

        if (location.getY() < minY || location.getY() > maxY - 3) {
            return false;
        }

        Block feet = location.getBlock();
        Block head = location.clone().add(0, 1, 0).getBlock();

        return feet.isPassable() && head.isPassable();
    }

}
