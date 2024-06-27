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
import me.calrl.hubbly.interfaces.CustomItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class ConfigItems implements CustomItem {
    private final JavaPlugin plugin;
    private final Logger logger;
    private final String itemKey;

    public ConfigItems(String itemKey) {
        this.plugin = Hubbly.getInstance();
        this.logger = plugin.getLogger();
        this.itemKey = itemKey;
    }

    @Override
    public ItemStack createItem() {
        return createConfigItem(itemKey);
    }

    public ItemStack createConfigItem(String itemKey) {
        FileConfiguration config = plugin.getConfig();
        String path = "items." + itemKey;
        Material material = null;

        try {
            material = Material.valueOf(config.getString(path + ".type"));
        } catch (IllegalArgumentException e) {
            logger.info("Failed to get material for item: " + e.getMessage());
        }

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            // Set item name
            if (config.contains(path + ".name")) {
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', config.getString(path + ".name")));
            }

            // Set item lore
            if (config.contains(path + ".lore")) {
                List<String> lore = new ArrayList<>();
                for (String line : config.getStringList(path + ".lore")) {
                    lore.add(ChatColor.translateAlternateColorCodes('&', line));
                }
                meta.setLore(lore);
            }
            List<String> commands = config.getStringList(path + ".commands");
            if (!commands.isEmpty()) {
                meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "customCommands"), PersistentDataType.STRING, String.join(";", commands));
            }

            item.setItemMeta(meta);
        }
        return item;
    }

    public void executeCommands(Player player, ItemStack item) {
        if (item == null || !item.hasItemMeta()) return;
        ItemMeta meta = item.getItemMeta();
        String commandsString = meta.getPersistentDataContainer().get(new NamespacedKey(plugin, "customCommands"), PersistentDataType.STRING);
        if (commandsString == null || commandsString.isEmpty()) return;

        String[] commands = commandsString.split(";");
        for (String command : commands) {
            if (command.startsWith("[player]")) {
                String playerCommand = command.substring(8).trim();
                player.performCommand(playerCommand);
            } else if (command.startsWith("[console]")) {
                String consoleCommand = command.substring(9).trim();
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), consoleCommand);
            }
        }
    }
}
