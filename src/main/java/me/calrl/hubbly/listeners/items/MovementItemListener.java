package me.calrl.hubbly.listeners.items;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.enums.PluginKeys;
import me.calrl.hubbly.enums.TridentSounds;
import me.calrl.hubbly.items.TridentItem;
import me.calrl.hubbly.managers.cooldown.CooldownType;
import net.minecraft.world.entity.projectile.EntityThrownTrident;
import net.minecraft.world.item.ItemFishingRod;
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
        if(!(event.getProjectile() instanceof Arrow arrow)) return;
        PersistentDataContainer container = arrow.getPersistentDataContainer();
        container.set(PluginKeys.ENDER_BOW.getKey(), PersistentDataType.STRING, "arrow");
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
                player.teleport(location);
                player.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
            }
        }
    }

    @EventHandler
    public void onTridentThrow(ProjectileLaunchEvent event) {
        if (event.getEntity() instanceof Trident trident && trident.getShooter() instanceof Player player) {
            ItemStack itemInHand = player.getInventory().getItemInMainHand();
            ItemMeta meta = itemInHand.getItemMeta();

            if(meta != null && itemInHand.getType() == Material.TRIDENT && itemInHand.getItemMeta().getPersistentDataContainer().has(PluginKeys.TRIDENT.getKey())) {
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
        if(!config.getBoolean("movementitems.trident.enabled")) return;
        if (event.getEntity() instanceof Trident trident && event.getEntity().getShooter() instanceof Player player) {

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
