package com.caldev.items;

import com.caldev.functions.CreateCustomHead;
import com.caldev.functions.ParsePlaceholders;
import com.caldev.interfaces.CustomItem;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Skull;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.io.File;
import java.lang.reflect.Field;
import java.util.UUID;
import java.util.logging.Logger;

public class SocialsItem implements CustomItem {

    private final FileConfiguration config;
    public SocialsItem(FileConfiguration config) {
        this.config = config;

    }

    public final ItemStack createItem() {
        ItemStack item;
        String textureValue = config.getString("socials.item.value");
        String itemName = config.getString("socials.item.name");
        String itemType = config.getString("socials.item.type");
        if("PLAYER_HEAD".equalsIgnoreCase(itemType)) {
            item = CreateCustomHead.createCustomHead(textureValue, itemName);
        } else {
            try {
                Material itemMat = Material.valueOf(itemType.toUpperCase());
                item = new ItemStack(itemMat);
                ItemMeta meta = item.getItemMeta();
                if(meta != null) {
                    meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', itemName));
                    item.setItemMeta(meta);
                }
            } catch (IllegalArgumentException e) {
                System.out.println(e);
                item = new ItemStack(Material.STONE);
            }

        }
        return item;
    }
}
