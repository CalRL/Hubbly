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
import me.calrl.hubbly.managers.DebugMode;
import me.calrl.hubbly.utils.ChatUtils;
import me.calrl.hubbly.utils.ItemBuilder;
import me.calrl.hubbly.utils.xseries.XMaterial;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class AoteItem implements CustomItem {

    private final Hubbly plugin;
    private Player player;
    public AoteItem(Hubbly plugin) {
        this.plugin = plugin;
    }

    @Override
    public ItemStack createItem() {
        FileConfiguration config = plugin.getConfig();

        ConfigurationSection section = config.getConfigurationSection("movementitems.aote");
        if(section == null) {
            plugin.getLogger().warning("movementitems.aote is null.");
            return null;
        }
        new DebugMode().info(section.getKeys(false).toString());
        ItemBuilder builder = new ItemBuilder();
        ItemStack item = builder
                .fromConfig(this.player, section)
                .addPersistentData(PluginKeys.AOTE.getKey(), PersistentDataType.STRING, "aote")
                .build();
        return item;
    }

    @Override
    public void setPlayer(Player player) {
        this.player = player;
    }
}
