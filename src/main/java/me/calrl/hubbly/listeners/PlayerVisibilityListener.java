package com.caldev.listeners;

import com.caldev.Hubbly;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.w3c.dom.Text;

public class PlayerVisibilityListener implements Listener {

    private final Hubbly plugin;
    private final FileConfiguration config;

    public PlayerVisibilityListener(Hubbly plugin, FileConfiguration config) {
        this.plugin = plugin;
        this.config = config;
    }
    @EventHandler
    public void onItemClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        if (itemInHand.getType() == Material.LIME_DYE || itemInHand.getType() == Material.GRAY_DYE) {
            if(event.getAction() != Action.PHYSICAL && player.hasPermission("hubbly.playervisibility.use") || player.isOp()) {
                if(player.hasPermission("hubbly.playervisibility.use")) {
                    event.setCancelled(true);
                    swapDye(player, itemInHand);
                } else {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("messages.no_permission_use")));
                }
            }

        }
    }
    private void swapDye(Player player, ItemStack itemInHand) {
        Material newMaterial;
        String displayName;
        if (itemInHand.getType() == Material.LIME_DYE) {
            newMaterial = Material.GRAY_DYE;
            displayName = ChatColor.translateAlternateColorCodes('&', "&rPlayers: &cHidden&r");
            for(Player online : Bukkit.getOnlinePlayers()){
                player.hidePlayer(plugin, online);
            }
        } else {
            newMaterial = Material.LIME_DYE;
            displayName = ChatColor.translateAlternateColorCodes('&', "&rPlayers: &aVisible&r");
            for(Player online : Bukkit.getOnlinePlayers()){
                player.showPlayer(plugin, online);
            }
        }
        ItemStack newItem = new ItemStack(newMaterial);
        ItemMeta meta = newItem.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(displayName);
            newItem.setItemMeta(meta);
        }
        player.getInventory().setItemInMainHand(newItem);
    }
}
