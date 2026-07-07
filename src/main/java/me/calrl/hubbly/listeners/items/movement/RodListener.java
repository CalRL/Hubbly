package me.calrl.hubbly.listeners.items.movement;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.enums.Permissions;
import me.calrl.hubbly.enums.PluginKeys;
import me.calrl.hubbly.managers.DebugMode;
import me.calrl.hubbly.managers.DisabledWorlds;
import me.calrl.hubbly.managers.cooldown.CooldownType;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

public class RodListener implements Listener {
    private Hubbly plugin;
    public RodListener(Hubbly plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    private void onFishingRodUse(PlayerFishEvent event) {
        FileConfiguration config = plugin.getConfig();
        DebugMode debugMode = new DebugMode(plugin);

        debugMode.info("Fishing rod event fired. State: " + event.getState());

        if (!config.getBoolean("movementitems.grappling_hook.enabled")) {
            debugMode.info("Grappling hook is disabled in config.");
            return;
        }

        Player player = event.getPlayer();
        DisabledWorlds disabledWorlds = plugin.services().disabledWorlds();

        if (disabledWorlds.inDisabledWorld(player.getLocation())) {
            debugMode.info("Player is in a disabled world: " + player.getWorld().getName());
            return;
        }

        if (!player.hasPermission(Permissions.USE_GRAPPLING_HOOK.getPermission())) {
            debugMode.info("Player lacks permission: " + Permissions.USE_GRAPPLING_HOOK.getPermission());
            return;
        }

        ItemMeta meta = player.getInventory().getItemInMainHand().getItemMeta();

        if (event.getState() != PlayerFishEvent.State.FAILED_ATTEMPT
                && event.getState() != PlayerFishEvent.State.IN_GROUND
                && event.getState() != PlayerFishEvent.State.REEL_IN) {
            debugMode.info("Ignored fish state: " + event.getState());
            return;
        }

        if (meta == null) {
            debugMode.info("Main hand item has no meta.");
            return;
        }

        if (!meta.getPersistentDataContainer().has(PluginKeys.GRAPPLING_HOOK.getKey())) {
            debugMode.info("Item is not a grappling hook.");
            return;
        }

        if (!plugin.services().cooldowns().tryCooldown(
                player.getUniqueId(),
                CooldownType.GRAPPLING_HOOK,
                config.getLong("movementitems.grappling_hook.cooldown")
        )) {
            debugMode.info("Grappling hook is on cooldown.");
            return;
        }

        Location hookLocation = event.getHook().getLocation();
        Location playerLocation = player.getLocation();

        double distance = hookLocation.distance(playerLocation);
        Vector direction = hookLocation.toVector().subtract(playerLocation.toVector()).normalize();

        Vector velocity = direction.multiply(distance / 3.5);

        debugMode.info("Launching player with grappling hook.");
        debugMode.info("Distance: " + distance);
        debugMode.info("Velocity: " + velocity);

        player.setVelocity(velocity);
    }
}
