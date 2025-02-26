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
import me.calrl.hubbly.enums.Permissions;
import me.calrl.hubbly.interfaces.CustomItem;
import me.calrl.hubbly.managers.DebugMode;
import me.calrl.hubbly.managers.ItemsManager;
import me.calrl.hubbly.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GiveCommand implements TabExecutor {

    private final Hubbly plugin;
    private final FileConfiguration config;
    private Map<String, CustomItem> items = new HashMap<>();
    private FileConfiguration itemsConfig;
    private ActionManager actionManager;
    private Map<String, Action> actions;
    private DebugMode debugMode;
    private ItemsManager itemsManager;

    public GiveCommand(Hubbly plugin) {
        this.plugin = plugin;
        this.itemsConfig = plugin.getItemsConfig();
        this.config = plugin.getConfig();
        this.actionManager = plugin.getActionManager();
        this.debugMode = plugin.getDebugMode();
        this.itemsManager = plugin.getItemsManager();
        items = itemsManager.getItems();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length < 3) {
            sender.sendMessage(ChatColor.YELLOW + "Usage: /give <player> <item> [amount] [slot]");
            return true;
        }

        if(!sender.hasPermission(Permissions.COMMAND_GIVE.getPermission())) {
            sender.sendMessage(ChatUtils.translateHexColorCodes(
                    config.getString("messages.no_permission_use", "No permission")
            ));
            return true;
        }

        Player targetPlayer = Bukkit.getPlayer(args[0]);
        if (targetPlayer == null) {
            sender.sendMessage(ChatColor.RED + "Player not found: " + args[0]);
            return true;
        }

        String itemName = ChatColor.stripColor(args[1].toLowerCase());
        CustomItem customItem = items.get(itemName);

        if (customItem == null) {
            sender.sendMessage(ChatColor.RED + "Unknown item: " + itemName);
            return true;
        }

        ItemStack item = customItem.createItem();
        if(args.length == 3) {
            int amount = Integer.parseInt(args[2]);
            item.setAmount(amount);
            targetPlayer.getInventory().addItem(item);
            return true;
        }


        if (args.length == 4) {
            try {
                int amount = Integer.parseInt(args[2]);
                item.setAmount(amount);

                int slot = Integer.parseInt(args[3]);
                if (slot < 0 || slot >= targetPlayer.getInventory().getSize()) {
                    sender.sendMessage(ChatColor.RED + "Invalid slot: " + args[3]);
                    return true;
                }

                targetPlayer.getInventory().setItem(slot-1, item);
                sender.sendMessage(ChatColor.YELLOW + "Given " + itemName + " to " + targetPlayer.getName() + " in slot " + slot);
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "Slot must be a number: " + args[3]);
            }
        } else {
            targetPlayer.getInventory().addItem(item);
            debugMode.info("Given " + itemName + " to " + targetPlayer.getName());
        }

        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        List<String> stringList = new ArrayList<>();
        if(args.length == 1) {
            for(Player player : Bukkit.getOnlinePlayers()) {
                stringList.add(player.getName());
            }
        }
        if(args.length == 2)
            stringList.addAll(items.keySet());
        return stringList;
    }
}
