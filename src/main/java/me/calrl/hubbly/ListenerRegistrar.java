package me.calrl.hubbly;

import me.calrl.hubbly.inventory.InventoryListener;
import me.calrl.hubbly.listeners.ServerLoadListener;
import me.calrl.hubbly.listeners.chat.ChatListener;
import me.calrl.hubbly.listeners.chat.CommandBlockerListener;
import me.calrl.hubbly.listeners.items.ConfigItemListener;
import me.calrl.hubbly.listeners.items.PlayerVisibilityListener;
import me.calrl.hubbly.listeners.items.movement.AoteListener;
import me.calrl.hubbly.listeners.items.movement.EnderbowListener;
import me.calrl.hubbly.listeners.items.movement.RodListener;
import me.calrl.hubbly.listeners.items.movement.TridentListener;
import me.calrl.hubbly.listeners.player.*;
import me.calrl.hubbly.listeners.world.LaunchpadListener;
import me.calrl.hubbly.listeners.world.WorldEventListeners;
import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;

import java.util.logging.Logger;

public class ListenerRegistrar {
    private final Hubbly plugin;
    public ListenerRegistrar(Hubbly plugin) {
        this.plugin = plugin;
        this.start();
        plugin.getLogger().info("Registered Listeners");
    }

    private void start() {
        registerListener(new ServerLoadListener(plugin));
        registerListener(new VoidDamageListener(plugin), "antivoid.enabled");
        registerListener(new DoubleJumpListener(plugin), "double_jump.enabled");
        registerListener(new PlayerVisibilityListener(), "playervisibility.enabled");
        registerListener(new LaunchpadListener(plugin), "launchpad.enabled");
        registerListener(new PlayerJoinListener(plugin));
        registerListener(new CommandBlockerListener(plugin));
        registerListener(new ForceinvListener(plugin), "player.forceinventory");
        registerListener(new ChatListener(plugin));
        registerListener(new InventoryListener(plugin));
        registerListener(new XPListener(plugin), "player.experience.enabled");
        registerListener(new PlayerMoveListener(plugin));
        registerListener(new EnderbowListener(plugin));
        registerListener(new AoteListener(plugin));
        registerListener(new RodListener(plugin));

        registerListener(new WorldEventListeners(plugin));
        registerListener(new ConfigItemListener(plugin));
        registerListener(new PlayerOffHandListener(plugin));
        registerListener(new WorldChangeListener(plugin));
    }

    private void registerListener(Listener listener, String enabledPath) {
        FileConfiguration config = this.plugin.getConfig();

        if(config.getBoolean(enabledPath) || enabledPath.equals("null")) {
            try {
                Server server = plugin.getServer();
                server.getPluginManager().registerEvents(listener, plugin);
            } catch(Exception e) {
                Logger logger = plugin.getLogger();
                logger.severe("PLEASE REPORT TO DEVELOPER");
                logger.severe(listener.getClass().getName()  + " failed to load, printing stacktrace...");
                logger.severe(e.getMessage());
            }
        }
    }

    private void registerListener(Listener listener) {
        registerListener(listener, "null");
    }
}
