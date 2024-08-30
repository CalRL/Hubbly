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
import me.calrl.hubbly.functions.ParsePlaceholders;
import me.calrl.hubbly.interfaces.CustomItem;
import me.calrl.hubbly.managers.DebugMode;
import me.calrl.hubbly.utils.ChatUtils;
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
import java.util.Objects;
import java.util.logging.Logger;

public class ConfigItems implements CustomItem {
    private final JavaPlugin plugin;
    private final Logger logger;
    private final String itemKey;
    private final ActionManager actionManager;
    private Player player;
    private DebugMode debugMode;

    public ConfigItems(String itemKey, Hubbly plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.itemKey = itemKey;
        this.actionManager = plugin.getActionManager();
        this.debugMode = plugin.getDebugMode();
    }

    @Override
    public ItemStack createItem() {
        return createConfigItem(itemKey, player);
    }

    public ItemStack createConfigItem(String itemKey, Player player) {
        FileConfiguration config = Hubbly.getInstance().getItemsConfig(); // Use itemsConfig for the items.yml
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
                meta.setDisplayName(ChatUtils.translateHexColorCodes(config.getString(path + ".name")));
            }

            // Set item lore
            if (config.contains(path + ".lore")) {
                List<String> lore = new ArrayList<>();
                for (String line : config.getStringList(path + ".lore")) {
                    lore.add(ChatUtils.translateHexColorCodes(ParsePlaceholders.parsePlaceholders(player, line)));
                }
                meta.setLore(lore);
            }
            if(config.contains(path + ".value") && Objects.equals(material, Material.valueOf(config.getString(path + ".type")))) {
                CreateCustomHead.createCustomHead(config.getString(path + ".value"), config.getString(".name"));
            }

            List<String> actions = config.getStringList(path + ".actions");
            if (!actions.isEmpty()) {

                String actionsString = String.join(",", actions);
                meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "customActions"), PersistentDataType.STRING, String.join(",", actions));
                debugMode.info("Set actions for item " + itemKey + ": " + actionsString);
            }

            item.setItemMeta(meta);
        }
        return item;
    }

    public void executeActions(Player player, ItemStack item) {
        if (item == null || !item.hasItemMeta()) return;
        ItemMeta meta = item.getItemMeta();
        String actionsString = meta.getPersistentDataContainer().get(new NamespacedKey(plugin, "customActions"), PersistentDataType.STRING);
        if (actionsString == null || actionsString.isEmpty()) return;

        String[] actions = actionsString.split(",");
        for (String actionData : actions) {
            actionManager.executeAction(Hubbly.getInstance(), player, actionData);
            debugMode.info("Executing actions: " + actionsString);
        }
    }
}
