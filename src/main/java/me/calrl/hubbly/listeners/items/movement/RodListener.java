package me.calrl.hubbly.listeners.items.movement;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.enums.Permissions;
import me.calrl.hubbly.enums.PluginKeys;
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
        if (!config.getBoolean("movementitems.grappling_hook.enabled")) return;

        Player player = event.getPlayer();
        DisabledWorlds disabledWorlds = plugin.getDisabledWorldsManager();
        if(disabledWorlds.inDisabledWorld(player.getLocation())) return;

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
}
