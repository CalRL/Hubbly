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
import me.calrl.hubbly.interfaces.CustomItem;
import me.calrl.hubbly.utils.ChatUtils;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class EnderbowItem implements CustomItem {
    private Hubbly plugin;

    public EnderbowItem(Hubbly plugin) {
        this.plugin = plugin;
    }
    @Override
    public ItemStack createItem() {
        FileConfiguration config = plugin.getConfig();
        ItemStack item = new ItemStack(Material.BOW);
        ItemMeta meta = item.getItemMeta();

        if(meta != null) {
            PersistentDataContainer container = meta.getPersistentDataContainer();
            container.set(PluginKeys.ENDER_BOW.getKey(), PersistentDataType.STRING, "enderbow");
            String itemName = ChatUtils.translateHexColorCodes(config.getString("movementitems.enderbow.name", "<#A020F0>Enderbow"));
            meta.setDisplayName(itemName);
            meta.setUnbreakable(true);
            item.setItemMeta(meta);
        }
        return item;
    }
}
