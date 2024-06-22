package com.caldev.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.event.Listener;

import java.util.Objects;
import java.util.logging.Logger;

public class ShopListener implements Listener {
    private final Logger logger;
    private final FileConfiguration config;

    public ShopListener(Logger logger, FileConfiguration config) {
        this.logger = logger;
        this.config = config;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        if (itemInHand.getType() == Material.valueOf(config.getString("shop.item.type")) && (player.hasPermission("hubbly.shop.use") || player.isOp())) {
            player.performCommand(Objects.requireNonNull(config.getString("shop.command")));
        }
    }
}
