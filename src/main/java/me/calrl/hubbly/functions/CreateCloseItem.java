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

package me.calrl.hubbly.functions;

import me.calrl.hubbly.interfaces.CustomItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Objects;


public class CreateCloseItem implements CustomItem {

    private final FileConfiguration config;
    public CreateCloseItem(FileConfiguration config) {
        this.config = config;
    }
    public final ItemStack createItem() {
        ItemStack item = new ItemStack(Material.valueOf(config.getString("close_button.material")));
        ItemMeta meta = item.getItemMeta();
        if(meta != null) {
            String itemName = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString("close_button.name")));
            meta.setDisplayName(itemName);
            item.setItemMeta(meta);
        }
        return item;
    }
}
