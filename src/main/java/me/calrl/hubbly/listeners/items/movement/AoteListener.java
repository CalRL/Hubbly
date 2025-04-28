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
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

public class AoteListener implements Listener {
    private Hubbly plugin;
    public AoteListener(Hubbly plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    private void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        DisabledWorlds disabledWorlds = plugin.getDisabledWorldsManager();
        if(disabledWorlds.inDisabledWorld(player.getLocation())) return;

        ItemStack item = player.getInventory().getItemInMainHand();
        ItemMeta meta = item.getItemMeta();
        if(meta != null && meta.getPersistentDataContainer().has(PluginKeys.AOTE.getKey())) {
            FileConfiguration config = plugin.getConfig();
            if(!plugin.getCooldownManager().tryCooldown(player.getUniqueId(), CooldownType.AOTE, config.getLong("movementitems.aote.cooldown"))) return;
            if(!player.hasPermission(Permissions.USE_AOTE.getPermission())) return;
            player.teleport(this.getLocationInFront(player, 10));
        }
    }

    private Location getLocationInFront(Player player, double distance) {
        Location currentLocation = player.getLocation();
        Vector direction = currentLocation.getDirection();
        Vector frontVector = direction.normalize().multiply(distance);

        return currentLocation.add(frontVector);
    }

}
