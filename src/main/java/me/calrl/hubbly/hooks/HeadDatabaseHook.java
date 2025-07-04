package me.calrl.hubbly.hooks;

import me.arcaniax.hdb.api.DatabaseLoadEvent;
import me.arcaniax.hdb.api.HeadDatabaseAPI;
import me.arcaniax.hdb.object.head.Head;
import me.calrl.hubbly.Hubbly;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import javax.xml.crypto.Data;

public class HeadDatabaseHook implements Hook, HeadHook, Listener {
    private HeadDatabaseAPI api;
    private Hubbly plugin;
    public HeadDatabaseHook() {

    }

    @Override
    public void onEnable(Hubbly plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        api = new HeadDatabaseAPI();
    }

    @Override
    public ItemStack getHead(String data) {
        return api.getItemHead(data);
    }

    public HeadDatabaseAPI getApi() {
        return this.api;
    }

    @EventHandler
    private void onDatabaseLoad(DatabaseLoadEvent event) {
        plugin.getLogger().info("Database Loaded");
    }
}
