package com.caldev.items;

import com.caldev.interfaces.CustomItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Objects;

public class PlayerVisibilityItem implements CustomItem {

    public final ItemStack createItem() {
        ItemStack item = new ItemStack(Material.LIME_DYE);
        ItemMeta meta = item.getItemMeta();
        if(meta != null) {
            String itemName = ChatColor.translateAlternateColorCodes('&', "&rPlayers: &aVisible&r");
            meta.setDisplayName(itemName);
            item.setItemMeta(meta);
        }
        return item;
    }

}
