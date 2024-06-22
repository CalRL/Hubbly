package com.caldev.listeners;

import java.util.Objects;
import java.util.logging.Logger;

import com.caldev.items.CompassItem;
import com.caldev.items.PlayerVisibilityItem;
import com.caldev.items.ShopItem;
import com.caldev.items.SocialsItem;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;

public class ItemJoinListener implements Listener {
    private final Logger logger;
    private final FileConfiguration config;
    private final JavaPlugin plugin;


    public ItemJoinListener(Logger logger, FileConfiguration config, JavaPlugin plugin) {
        this.logger = logger;
        this.config = config;
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        PlayerInventory playerInventory = event.getPlayer().getInventory();
        playerInventory.clear();
        playerInventory.setHeldItemSlot(4);
        if(Objects.equals(config.getBoolean("item_on_join.enabled"), true)) {
            if(Objects.equals(config.getBoolean("item_on_join.features.compass.enabled"), true)) {
                event.getPlayer().getInventory().setItem(config.getInt("item_on_join.features.compass.slot")-1, new CompassItem(config).createItem());
            }
            if(Objects.equals(config.getBoolean("item_on_join.features.shop.enabled"), true)) {
                event.getPlayer().getInventory().setItem(config.getInt("item_on_join.features.shop.slot")-1, new ShopItem(config).createItem());
            }
            if(Objects.equals(config.getBoolean("item_on_join.features.socials.enabled"), true)) {
                event.getPlayer().getInventory().setItem(config.getInt("item_on_join.features.socials.slot")-1, new SocialsItem(config).createItem());
            }
            if(Objects.equals(config.getBoolean("item_on_join.features.playervisibility.enabled"), true)) {
                event.getPlayer().getInventory().setItem(config.getInt("item_on_join.features.playervisibility.slot")-1, new PlayerVisibilityItem().createItem());
            }

        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        boolean forceInventory = plugin.getConfig().getBoolean("item_on_join.forceinventory");
        Player player = (Player) event.getWhoClicked();

        if (forceInventory && !player.hasPermission("hubbly.items.bypass")) {
            event.setCancelled(true);
            player.setItemOnCursor(new ItemStack(Material.AIR));
            player.updateInventory();
        }
    }

}
