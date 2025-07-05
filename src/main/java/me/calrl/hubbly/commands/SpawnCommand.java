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
import me.calrl.hubbly.enums.LocaleKey;
import me.calrl.hubbly.events.HubblySpawnEvent;
import me.calrl.hubbly.interfaces.CustomItem;
import me.calrl.hubbly.utils.MessageBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpawnCommand implements TabExecutor {

    private final Hubbly plugin;
    private FileConfiguration config;
    private final Map<String, CustomItem> items = new HashMap<>();

    public SpawnCommand(Hubbly plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        config = plugin.getConfig();


        if(!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + config.getString("messages.no_console"));
            return true;
        }

        Location spawn = plugin.getUtils().getSpawn();
        HubblySpawnEvent event = new HubblySpawnEvent(player, spawn);
        Bukkit.getPluginManager().callEvent(event);

        if(!player.hasPermission("hubbly.command.spawn")) {
            new MessageBuilder(plugin)
                    .setPlayer(player)
                    .setKey("no_permission_command")
                    .send();
            return true;
        }

        if(!event.isCancelled()) {
            Bukkit.getScheduler().runTaskLater(plugin, ()-> {
                player.teleport(spawn);
            }, 1L);

        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!(sender instanceof Player player)) return null;

        final List<String> completions = new ArrayList<>();

        return completions;
    }
}