package me.calrl.hubbly.handlers;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.enums.PluginKeys;
import me.calrl.hubbly.enums.data.PlayerMovementMode;
import me.calrl.hubbly.managers.DebugMode;
import me.calrl.hubbly.managers.StorageManager;
import me.calrl.hubbly.managers.cooldown.CooldownType;
import me.calrl.hubbly.storage.PlayerData;
import me.calrl.hubbly.storage.PlayerMovementData;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.UUID;

public class PlayerMovementHandler {
    private final Player player;
    private final Hubbly plugin;

    public PlayerMovementHandler(Player player, Hubbly plugin) {
        this.player = player;
        this.plugin = plugin;
    }

    public void handleMovement(PlayerToggleFlightEvent event) {
        if(this.getMovementMode() != PlayerMovementMode.DOUBLEJUMP) return;
        FileConfiguration config = this.plugin.getConfig();
        new DebugMode(plugin).info("HANDLEMOVEMENT event cancel");
        event.setCancelled(true);
        player.setFlying(false);
        player.setAllowFlight(false);
        UUID uuid = player.getUniqueId();
        new DebugMode(plugin).info("cooldown");
        if(!plugin.getCooldownManager().tryCooldown(uuid, CooldownType.DOUBLE_JUMP, config.getLong("double_jump.cooldown"))) return;

        Vector direction = player.getLocation().getDirection();
        direction.setY(config.getDouble("double_jump.power_y"));
        direction.multiply(config.getDouble("double_jump.power", 1.0));

        player.setVelocity(direction);
        new DebugMode(plugin).info("runnable");
        new BukkitRunnable() {
            @Override
            public void run() {
                Location location = player.getLocation();
                if(!plugin.getDisabledWorldsManager().inDisabledWorld(location)) {
                    player.setAllowFlight(true);
                }
            }
        }.runTaskLater(plugin, plugin.getConfig().getLong("double_jump.cooldown") * 20L);
    }

    public PlayerMovementMode getMovementMode() {
        // Get from PlayerMovementData

        PersistentDataContainer container = player.getPersistentDataContainer();
        if(container.has(PluginKeys.MOVEMENT_KEY.getKey())) {
            String value = container.get(PluginKeys.MOVEMENT_KEY.getKey(), PersistentDataType.STRING);
            return PlayerMovementMode.valueOf(value);
        }

        return null;
    }

    public void setMovementMode(PlayerMovementMode mode) {
        new DebugMode(plugin).info(String.format("Setting movement mode: %s", mode.toString()));
        PersistentDataContainer container = this.player.getPersistentDataContainer();

        container.set(PluginKeys.MOVEMENT_KEY.getKey(), PersistentDataType.STRING, mode.getString());
        player.setAllowFlight(mode != PlayerMovementMode.NONE);
        player.setFlying(false);

        FileConfiguration config = plugin.getConfig();
        StorageManager storage = plugin.getStorageManager();
        if(config.getBoolean("database.enabled") && storage.isActive()) {
            new DebugMode(plugin).info(String.format("Saving snapshot for player: %s", player.getName()));
            storage.enqueueSave(PlayerData.from(player));
        }
    }

    public void applyMode() {
        PersistentDataContainer container = this.player.getPersistentDataContainer();
        NamespacedKey key = PluginKeys.MOVEMENT_KEY.getKey();
        assert container.has(key);

        String val = container.get(key, PersistentDataType.STRING);
        PlayerMovementMode mode = PlayerMovementMode.valueOf(val);

        player.setAllowFlight(mode != PlayerMovementMode.NONE);
        player.setFlying(false);

    }

    public void handleJoin(PlayerData data) {
        new DebugMode(plugin).info("handlejoin");
        PersistentDataContainer container = player.getPersistentDataContainer();

        FileConfiguration config = plugin.getConfig();
        StorageManager storage = plugin.getStorageManager();

        if(config.getBoolean("database.enabled") && storage.isActive()) {
            if(data != null) {
                PlayerMovementData mvData = data.movement();
                this.setMovementMode(mvData.getMode());
                return;
            }
        } else {
            // database disabled
            NamespacedKey key = PluginKeys.MOVEMENT_KEY.getKey();
            if(container.has(key)) {
                this.applyMode();
                return;
            }
        }
        this.setMovementMode(PlayerMovementMode.NONE);
    }
}