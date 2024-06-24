/*
 * This file is part of Hubbly.
 *
 * Hubbly is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Hubbly is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Hubbly. If not, see <http://www.gnu.org/licenses/>.
 */

package me.calrl.hubbly.items;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.functions.CreateCustomHead;
import me.calrl.hubbly.functions.ParsePlaceholders;
import me.calrl.hubbly.interfaces.CustomItem;
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

    private FileConfiguration config = Hubbly.getInstance().getConfig();

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
