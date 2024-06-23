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

import me.calrl.hubbly.interfaces.CustomItem;
import me.calrl.hubbly.interfaces.SubCommand;
import me.calrl.hubbly.items.CompassItem;
import me.calrl.hubbly.items.PlayerVisibilityItem;
import me.calrl.hubbly.items.ShopItem;
import me.calrl.hubbly.items.SocialsItem;
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

    public GiveCommand(JavaPlugin plugin, FileConfiguration config) {
        this.plugin = plugin;
        this.config = config;
        registerItems();
    }

    private void registerItems() {
        items.put("compass", new CompassItem(config));
        items.put("socials", new SocialsItem(config));
        items.put("shop", new ShopItem(config));
        items.put("playervisibility", new PlayerVisibilityItem());
    }
    @Override
    public void execute(Player player, String[] args) {
        if (args.length < 2 && player.hasPermission("hubbly.command.give") || player.isOp()) {
            player.sendMessage(ChatColor.YELLOW + "Usage: /hubbly give <item>");
            return;
        }
        if(player.hasPermission("hubbly.command.give") || player.isOp()) {
            String itemName = args[1].toLowerCase();
            CustomItem customItem = items.get(itemName);

            if (customItem != null) {
                ItemStack item = customItem.createItem();
                player.getInventory().addItem(item);
                player.sendMessage(ChatColor.YELLOW + "Given " + itemName);
            } else {
                player.sendMessage(ChatColor.RED + "Unknown item.");
            }
        } else {
            player.sendMessage(Objects.requireNonNull(config.getString("messages.no_permission_use/")));
        }

    }
}
