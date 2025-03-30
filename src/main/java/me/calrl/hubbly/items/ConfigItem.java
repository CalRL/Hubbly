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
import me.calrl.hubbly.action.ActionManager;
import me.calrl.hubbly.functions.CreateCustomHead;
import me.calrl.hubbly.interfaces.CustomItem;
import me.calrl.hubbly.managers.DebugMode;
import me.calrl.hubbly.managers.FileManager;
import me.calrl.hubbly.managers.ItemsManager;
import me.calrl.hubbly.utils.ChatUtils;
import me.calrl.hubbly.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

public class ConfigItem implements CustomItem {
    private final Hubbly plugin;
    private final Logger logger;
    private final String itemKey;
    private final ActionManager actionManager;
    private Player player;
    private final DebugMode debugMode;

    public ConfigItem(String itemKey, Hubbly plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.itemKey = itemKey;
        this.actionManager = plugin.getActionManager();
        this.debugMode = plugin.getDebugMode();
    }

    @Override
    public ItemStack createItem() {
        FileManager manager = plugin.getFileManager();
        FileConfiguration config = manager.getConfig("items.yml");

        if (!config.contains("items." + itemKey)) {
            debugMode.warn("Item '" + itemKey + "' not found in items.yml.");
            return null;
        }

        ConfigurationSection section = config.getConfigurationSection("items." + itemKey);
        if (section == null) {
            debugMode.warn("Section for item '" + itemKey + "' is null.");
            return null;
        }

        ItemStack item = new ItemBuilder()
                .fromConfig(player, section)
                .build();

        if (item == null || item.getType() == Material.AIR) {
            debugMode.warn("Item '" + itemKey + "' is invalid or returned AIR.");
            return null;
        }

        return item;
    }

    @Override
    public void setPlayer(Player player) {
        this.player = player;
    }


}
