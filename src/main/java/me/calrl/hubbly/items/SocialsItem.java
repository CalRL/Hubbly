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
import me.calrl.hubbly.enums.PluginKeys;
import me.calrl.hubbly.functions.CreateCustomHead;
import me.calrl.hubbly.interfaces.CustomItem;
import me.calrl.hubbly.utils.ChatUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class SocialsItem implements CustomItem {

    private FileConfiguration config = Hubbly.getInstance().getConfig();

    public final ItemStack createItem() {
        ItemStack item;
        String textureValue = config.getString("socials.item.value");
        String itemName = ChatUtils.translateHexColorCodes(config.getString("socials.item.name"));

        String itemType = config.getString("socials.item.type");
        if(itemType == null) { return null; }

        ItemMeta meta;

        if("PLAYER_HEAD".equalsIgnoreCase(itemType)) {
            item = CreateCustomHead.createCustomHead(textureValue, itemName);

        } else {
            Material itemMat = Material.valueOf(itemType.toUpperCase());
            item = new ItemStack(itemMat);
            meta = item.getItemMeta();

            if(meta != null) {
                meta.setDisplayName(ChatUtils.translateHexColorCodes(itemName));
            }
        }
        meta = item.getItemMeta();
        if(meta == null) return null;

        PersistentDataContainer container = meta.getPersistentDataContainer();
        container.set(PluginKeys.SOCIALS.getKey(), PersistentDataType.STRING, "socials");

        item.setItemMeta(meta);

        return item;
    }
}
