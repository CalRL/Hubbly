package me.calrl.hubbly.listeners.items.movement;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.enums.Permissions;
import me.calrl.hubbly.enums.PluginKeys;
import me.calrl.hubbly.enums.TridentSounds;
import me.calrl.hubbly.items.PlayerTridentData;
import me.calrl.hubbly.managers.DebugMode;
import me.calrl.hubbly.managers.DisabledWorlds;
import me.calrl.hubbly.managers.TridentDataManager;
import me.calrl.hubbly.managers.cooldown.CooldownType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Trident;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class TridentListener implements Listener {
    private TridentDataManager tridentManager;
    private Hubbly plugin;
    private FileConfiguration config;
    public TridentListener(Hubbly plugin) {
        this.plugin = plugin;
    }

    public int getVoidLevel(World world) {
        if (world.getEnvironment() == World.Environment.NETHER) {
            return 0;
        }
        return -64;
    }


    @EventHandler
    public void onTridentLaunch(ProjectileLaunchEvent event) {
        this.config = plugin.getConfig();

        if (!config.getBoolean("movementitems.trident.enabled")) return;
        if (!(event.getEntity() instanceof Trident trident)) return;
        if (!(trident.getShooter() instanceof Player player)) return;

        DisabledWorlds disabledWorlds = plugin.getDisabledWorldsManager();
        if (disabledWorlds.inDisabledWorld(player.getLocation())) return;
        if (!player.hasPermission(Permissions.USE_TRIDENT.getPermission())) return;

        PlayerInventory playerInventory = player.getInventory();
        ItemStack itemInHand = playerInventory.getItemInMainHand();

        if (itemInHand.getType() != Material.TRIDENT) return;

        ItemMeta meta = itemInHand.getItemMeta();
        if (meta == null) return;

        if (!meta.getPersistentDataContainer().has(PluginKeys.TRIDENT.getKey())) return;

        int slot = playerInventory.getHeldItemSlot();

        boolean cooldown = plugin.getCooldownManager()
                .tryCooldown(player.getUniqueId(),
                        CooldownType.TRIDENT,
                        config.getLong("movementitems.trident.cooldown"));

        if (!cooldown) {
            new DebugMode(plugin).info(Long.toString(
                    plugin.getCooldownManager()
                            .getCooldown(player.getUniqueId(), CooldownType.TRIDENT)));
            event.setCancelled(true);
            return;
        }

        trident.getPersistentDataContainer().set(
                PluginKeys.TRIDENT.getKey(),
                PersistentDataType.STRING,
                player.getName()
        );

        ItemStack newTrident = itemInHand.clone();
        plugin.services().tridentDataManager().add(player, newTrident, slot);
        new DebugMode().info("Starting tracking trident");

        new BukkitRunnable() {
            @Override
            public void run() {

                if (!trident.isValid() || trident.isDead()) {
                    cancel();
                    return;
                }

                World world = trident.getWorld();
                if (trident.getLocation().getY() < getVoidLevel(world)) {
                    replace(player);
                    trident.remove();
                    new DebugMode().info("Cleaned up voided trident");
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }


    private void replace(Player player) {
        PlayerTridentData data = plugin.services().tridentDataManager().get(player);
        ItemStack newTrident = data.getTrident();
        int slot = data.getSlot();

        PlayerInventory inventory = player.getInventory();
        ItemStack slotItem = inventory.getItem(slot);

        if(slotItem == null) {
            player.getInventory().setItem(slot, newTrident);
        } else {
            player.getInventory().addItem(newTrident);
        }
        plugin.services().tridentDataManager().remove(player);
    }


    @EventHandler
    private void onTridentDelete(EntityDeathEvent event) {
        Entity entity = event.getEntity();

        if(entity.getType() != EntityType.TRIDENT) {
            return;
        }

        EntityDamageEvent lastDamageCause = entity.getLastDamageCause();
        if(lastDamageCause == null) {
            return;
        }

        EntityDamageEvent.DamageCause cause = lastDamageCause.getCause();
        if(cause == EntityDamageEvent.DamageCause.VOID) {

            PersistentDataContainer container = entity.getPersistentDataContainer();
            if(!container.has(PluginKeys.TRIDENT.getKey())) {
                return;
            }

            String playerName = container.get(PluginKeys.TRIDENT.getKey(), PersistentDataType.STRING);
            if(playerName == null) {
                new DebugMode().info("No playername on trident");
                return;
            }
            Player player = Bukkit.getPlayer(playerName);

            this.replace(player);
        }
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

            this.replace(player);
        }
    }

    private TridentSounds getRandomSound() {
        int pick = new Random().nextInt(TridentSounds.values().length);
        return TridentSounds.values()[pick];
    }
}
