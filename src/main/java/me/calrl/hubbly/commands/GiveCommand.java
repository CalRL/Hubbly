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

package me.calrl.hubbly.commands;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.interfaces.CustomItem;
import me.calrl.hubbly.interfaces.SubCommand;
import me.calrl.hubbly.items.*;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class GiveCommand implements SubCommand {

    private final JavaPlugin plugin;
    private final FileConfiguration config;
    private final Map<String, CustomItem> items = new HashMap<>();

    public GiveCommand(JavaPlugin plugin) {
        this.plugin = plugin;
        this.config = Hubbly.getInstance().getConfig();
        registerItems();
    }

    private void registerItems() {
        items.put("compass", new CompassItem());
        items.put("socials", new SocialsItem());
        items.put("shop", new ShopItem());
        items.put("playervisibility", new PlayerVisibilityItem());

        for (String itemKey : config.getConfigurationSection("items").getKeys(false)) {
            items.put(ChatColor.stripColor(itemKey.toLowerCase()), new ConfigItems(itemKey));
        }
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length < 2 && (player.hasPermission("hubbly.command.give") || player.isOp())) {
            player.sendMessage(ChatColor.YELLOW + "Usage: /hubbly give <item>");
            return;
        }

        if (player.hasPermission("hubbly.command.give") || player.isOp()) {
            String itemName = ChatColor.stripColor(args[1].toLowerCase());
            CustomItem customItem = items.get(itemName);

            if (customItem != null) {
                ItemStack item = customItem.createItem();
                player.getInventory().addItem(item);
                player.sendMessage(ChatColor.YELLOW + "Given " + itemName);
            } else {
                player.sendMessage(ChatColor.RED + "Unknown item.");
            }
        } else {
            player.sendMessage(Objects.requireNonNull(config.getString("messages.no_permission_use")));
        }
    }
}
