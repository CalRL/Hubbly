package com.caldev.items;


import com.caldev.interfaces.CustomItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Objects;
import java.util.logging.Logger;

public class CompassItem implements CustomItem {


    private final FileConfiguration config;
    public CompassItem(FileConfiguration config) {
        this.config = config;
    }

    public final ItemStack createItem() {
        ItemStack compass = new ItemStack(Material.COMPASS);
        ItemMeta meta = compass.getItemMeta();
        if(meta != null) {
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString("compass.name"))));
            compass.setItemMeta(meta);
        }
        return compass;
    }
}
