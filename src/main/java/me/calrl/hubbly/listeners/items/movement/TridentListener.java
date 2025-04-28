package me.calrl.hubbly.listeners.items.movement;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.enums.Permissions;
import me.calrl.hubbly.enums.PluginKeys;
import me.calrl.hubbly.enums.TridentSounds;
import me.calrl.hubbly.items.PlayerTridentData;
import me.calrl.hubbly.managers.DisabledWorlds;
import me.calrl.hubbly.managers.TridentDataManager;
import me.calrl.hubbly.managers.cooldown.CooldownType;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.entity.Trident;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Random;

public class TridentListener implements Listener {
    private TridentDataManager tridentManager;
    private Hubbly plugin;
    private FileConfiguration config;
    public TridentListener(Hubbly plugin) {
        this.plugin = plugin;
        this.tridentManager = new TridentDataManager();
    }

    @EventHandler
    public void onTridentThrow(ProjectileLaunchEvent event) {
        this.config = plugin.getConfig();

        if(!config.getBoolean("movementitems.trident.enabled")) return;
        if (!(event.getEntity() instanceof Trident trident) || !(trident.getShooter() instanceof Player player)) {
            return;
        }

        DisabledWorlds disabledWorlds = plugin.getDisabledWorldsManager();
        if(disabledWorlds.inDisabledWorld(player.getLocation())) return;
        if(!player.hasPermission(Permissions.USE_TRIDENT.getPermission())) return;

        PlayerInventory playerInventory = player.getInventory();
        ItemStack itemInHand = playerInventory.getItemInMainHand();

        ItemMeta meta = itemInHand.getItemMeta();
        int slot = playerInventory.getHeldItemSlot();

        if(meta != null && itemInHand.getType() == Material.TRIDENT && meta.getPersistentDataContainer().has(PluginKeys.TRIDENT.getKey())) {

            if(!plugin.getCooldownManager().tryCooldown(player.getUniqueId(), CooldownType.TRIDENT, config.getLong("movementitems.trident.cooldown"))) {
                event.setCancelled(true);
                return;
            }

            event.getEntity().getPersistentDataContainer().set(PluginKeys.TRIDENT.getKey(), PersistentDataType.STRING, "trident");
        }
        ItemStack newTrident = itemInHand.clone();

        tridentManager.add(player, newTrident, slot);
    }

    @EventHandler
    private void onTridentLand(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof Trident trident) || !(event.getEntity().getShooter() instanceof Player player)) {
            return;
        }

        this.config = plugin.getConfig();
        if(!config.getBoolean("movementitems.trident.enabled")) return;

        DisabledWorlds disabledWorlds = plugin.getDisabledWorldsManager();
        if(disabledWorlds.inDisabledWorld(player.getLocation())) return;


        if (trident.getPersistentDataContainer().has(PluginKeys.TRIDENT.getKey())) {
            trident.remove();

            player.teleport(trident.getLocation().setDirection(player.getLocation().getDirection()));
            player.playSound(player.getLocation(), this.getRandomSound().getSound(), 1.0F, 1.0F);

            PlayerTridentData data = tridentManager.get(player);
            ItemStack newTrident = data.getTrident();
            int slot = data.getSlot();

            PlayerInventory inventory = player.getInventory();
            ItemStack slotItem = inventory.getItem(slot);

            if(slotItem == null) {
                player.getInventory().setItem(slot, newTrident);
            } else {
                player.getInventory().addItem(newTrident);
            }
            tridentManager.remove(player);
        }
    }

    private TridentSounds getRandomSound() {
        int pick = new Random().nextInt(TridentSounds.values().length);
        return TridentSounds.values()[pick];
    }
}
