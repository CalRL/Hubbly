package me.calrl.hubbly.listeners.items.movement;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.enums.Permissions;
import me.calrl.hubbly.enums.PluginKeys;
import me.calrl.hubbly.managers.DisabledWorlds;
import me.calrl.hubbly.managers.cooldown.CooldownType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.event.Listener;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class EnderbowListener implements Listener {
    private Hubbly plugin;
    public EnderbowListener(Hubbly plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event) {
        FileConfiguration config = plugin.getConfig();
        if(!config.getBoolean("movementitems.enderbow.enabled")) return;

        DisabledWorlds disabledWorlds = plugin.getDisabledWorldsManager();

        final Player player = event.getPlayer();
        if(disabledWorlds.inDisabledWorld(player.getWorld())) return;

        if(!player.hasPermission(Permissions.USE_ENDER_BOW.getPermission())) {
            return;
        }

        player.getInventory().setItem(17, new ItemStack(Material.ARROW));
    }

    @EventHandler
    private void onInteract(PlayerInteractEvent event) {
        FileConfiguration config = plugin.getConfig();
        if(!config.getBoolean("movementitems.enderbow.enabled")) {
            plugin.getDebugMode().info("Ender Bow is disabled in config.");
            return;
        }

        DisabledWorlds disabledWorlds = plugin.getDisabledWorldsManager();
        Player player = event.getPlayer();

        if(disabledWorlds.inDisabledWorld(event.getPlayer().getLocation())) {
            return;
        }

        if(!player.hasPermission(Permissions.USE_ENDER_BOW.getPermission())) {
            return;
        }

        ItemStack item = event.getItem();
        if(item == null) {
            return;
        }

        if(item.getType() != Material.BOW) {
            return;
        }

        ItemMeta meta = item.getItemMeta();
        if(meta == null) {
            plugin.getDebugMode().info("Item has no meta.");
            return;
        }

        PersistentDataContainer container = meta.getPersistentDataContainer();
        if(!container.has(PluginKeys.ENDER_BOW.getKey())) {
            return;
        }

        PlayerInventory inventory = player.getInventory();
        if(inventory.contains(Material.ARROW)) {
            plugin.getDebugMode().info("Player already has an arrow.");
            return;
        }

        ItemStack slotItem = inventory.getItem(17);
        ItemStack arrow = new ItemStack(Material.ARROW);

        if(slotItem == null) {
            plugin.getDebugMode().info("Slot 17 is empty, placing arrow.");
            inventory.setItem(17, arrow);
        } else {
            plugin.getDebugMode().info("Slot 17 occupied, adding arrow to inventory.");
            inventory.addItem(arrow);
        }
    }

    @EventHandler
    private void onBowShoot(EntityShootBowEvent event) {
        FileConfiguration config = plugin.getConfig();
        if(!config.getBoolean("movementitems.enderbow.enabled")) {
            plugin.getDebugMode().info("Ender Bow is disabled in config.");
            return;
        }

        if (!(event.getEntity() instanceof Player player)) return;

        DisabledWorlds disabledWorlds = plugin.getDisabledWorldsManager();
        if(disabledWorlds.inDisabledWorld(player.getLocation())) {
            plugin.getDebugMode().info("Entity is in a disabled world.");
            return;
        }

        ItemStack bow = event.getBow();
        if (bow == null) return;

        ItemMeta meta = bow.getItemMeta();
        if (meta == null) return;

        PersistentDataContainer bowContainer = meta.getPersistentDataContainer();
        if (!bowContainer.has(PluginKeys.ENDER_BOW.getKey())) return;

        if(!plugin.getCooldownManager().tryCooldown(player.getUniqueId(), CooldownType.ENDER_BOW, config.getLong("movementitems.enderbow.cooldown"))) {
            plugin.getDebugMode().info("Cooldown not expired, cancelling shot.");
            event.setCancelled(true);
            return;
        }

        if(!player.hasPermission(Permissions.USE_ENDER_BOW.getPermission())) {
            plugin.getDebugMode().info("Player does not have permission to shoot Ender Bow.");
            player.sendMessage(String.valueOf(player.hasPermission(Permissions.USE_ENDER_BOW.getPermission())));
            event.setCancelled(true);
            return;
        }

        if(!(event.getProjectile() instanceof Arrow arrow)) {
            plugin.getDebugMode().info("Projectile is not an arrow.");
            return;
        }

        plugin.getDebugMode().info("Marking shot arrow with Ender Bow data.");
        PersistentDataContainer container = arrow.getPersistentDataContainer();
        container.set(PluginKeys.ENDER_BOW.getKey(), PersistentDataType.STRING, "arrow");

        plugin.getDebugMode().info("Giving player a new arrow in slot 17.");
        player.getInventory().setItem(17, new ItemStack(Material.ARROW));

    }

    @EventHandler
    private void onArrowLand(ProjectileHitEvent event) {
        FileConfiguration config = plugin.getConfig();
        if(!config.getBoolean("movementitems.enderbow.enabled")) return;

        DisabledWorlds disabledWorlds = plugin.getDisabledWorldsManager();
        if(disabledWorlds.inDisabledWorld(event.getEntity().getWorld())) return;

        if(!(event.getEntity().getShooter() instanceof Player player)) return;
        if(event.getEntity() instanceof Arrow arrow) {
            PersistentDataContainer container = arrow.getPersistentDataContainer();
            if(!container.has(PluginKeys.ENDER_BOW.getKey())) {
                return;
            }

            Location location = arrow.getLocation();
            location.setPitch(player.getLocation().getPitch());
            location.setYaw(player.getLocation().getYaw());
            arrow.remove();
            player.teleport(location);
            player.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);

        }
    }

}
