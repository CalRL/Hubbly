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
import me.calrl.hubbly.action.Action;
import me.calrl.hubbly.action.ActionManager;
import me.calrl.hubbly.interfaces.CustomItem;
import me.calrl.hubbly.interfaces.SubCommand;
import me.calrl.hubbly.items.*;
import me.calrl.hubbly.utils.ChatUtils;
import org.bukkit.Bukkit;
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
    private FileConfiguration itemsConfig;
    private ActionManager actionManager;
    private Map<String, Action> actions;

    public GiveCommand(JavaPlugin plugin) {
        this.plugin = plugin;
        this.itemsConfig = Hubbly.getInstance().getItemsConfig();
        this.config = Hubbly.getInstance().getConfig();
        this.actionManager = new ActionManager();
        registerItems();
    }

    private void registerItems() {
        items.put("compass", new CompassItem());
        items.put("socials", new SocialsItem());
        items.put("shop", new ShopItem());
        items.put("playervisibility", new PlayerVisibilityItem());

        if (itemsConfig.getConfigurationSection("items") != null) {
            for (String itemKey : itemsConfig.getConfigurationSection("items").getKeys(false)) {
                items.put(ChatColor.stripColor(itemKey.toLowerCase()), new ConfigItems(itemKey, actionManager));
            }
        } else {
            plugin.getLogger().warning("No items found in items.yml");
        }
    }

    @Override
    public void execute(Player sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(ChatColor.YELLOW + "Usage: /hubbly give <player> <item> [slot]");
            return;
        }

        if (sender.hasPermission("hubbly.command.give") || sender.isOp()) {
            Player targetPlayer = Bukkit.getPlayer(args[1]);
            if (targetPlayer == null) {
                sender.sendMessage(ChatColor.RED + "Player not found: " + args[1]);
                return;
            }

            String itemName = ChatColor.stripColor(args[2].toLowerCase());
            CustomItem customItem = items.get(itemName);

            if (customItem != null) {
                ItemStack item = customItem.createItem();
                if (args.length > 3) {
                    try {
                        int slot = Integer.parseInt(args[3]);
                        if (slot < 0 || slot >= targetPlayer.getInventory().getSize()) {
                            sender.sendMessage(ChatColor.RED + "Invalid slot: " + args[3]);
                            return;
                        }
                        targetPlayer.getInventory().setItem(slot, item);
                        sender.sendMessage(ChatColor.YELLOW + "Given " + itemName + " to " + targetPlayer.getName() + " in slot " + slot);
                    } catch (NumberFormatException e) {
                        sender.sendMessage(ChatColor.RED + "Slot must be a number: " + args[3]);
                    }
                } else {
                    targetPlayer.getInventory().addItem(item);
                    sender.sendMessage(ChatColor.YELLOW + "Given " + itemName + " to " + targetPlayer.getName());
                }
            } else {
                sender.sendMessage(ChatColor.RED + "Unknown item: " + itemName); // Debugging
            }
        } else {
            sender.sendMessage(Objects.requireNonNull(ChatUtils.translateHexColorCodes(config.getString("messages.no_permission_use"))));
        }
    }
}
