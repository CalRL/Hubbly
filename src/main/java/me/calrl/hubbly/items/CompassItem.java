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
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

public class CompassItem implements CustomItem {

    private final Hubbly plugin;
    private final FileConfiguration config;
    public CompassItem(Hubbly plugin) {
        this.plugin = plugin;
        this.config = plugin.getServerSelectorConfig();
    }

    public final ItemStack createItem() {
        ItemStack compass = new ItemStack(Material.valueOf(config.getString("selector.material").toUpperCase()));
        ItemMeta meta = compass.getItemMeta();
        if(meta != null) {
            try {
                String displayName = ChatUtils.translateHexColorCodes(
                        config.getString("selector.name"));
                meta.getPersistentDataContainer().set(PluginKeys.SELECTOR.getKey(), PersistentDataType.STRING, "selector");
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
                compass.setItemMeta(meta);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
        return compass;
    }
}