package me.calrl.hubbly.listeners;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.hooks.HookManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;

public class ServerLoadListener implements Listener {
    private Hubbly plugin;
    public ServerLoadListener(Hubbly plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    private void onServerLoad(ServerLoadEvent event) {
        HookManager hookManager = new HookManager(plugin);

        plugin.setHookManager(hookManager);
        plugin.getLogger().info("Set hook manager");
    }
}

