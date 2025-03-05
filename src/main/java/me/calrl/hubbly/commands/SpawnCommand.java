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
import me.calrl.hubbly.events.HubblySpawnEvent;
import me.calrl.hubbly.interfaces.CustomItem;
import me.calrl.hubbly.utils.ChatUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class SpawnCommand implements CommandExecutor {

    private final Hubbly plugin;
    private FileConfiguration config;
    private final Map<String, CustomItem> items = new HashMap<>();

    public SpawnCommand(Hubbly plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        if(!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + config.getString("messages.no_console"));
            return true;
        }
        Location spawn = plugin.getUtils().getSpawn();
        HubblySpawnEvent event = new HubblySpawnEvent(player, spawn);

        if(!sender.hasPermission("hubbly.command.spawn")) {
            String noPermission = config.getString("messages.no_permission_command");
            player.sendMessage(
                    ChatUtils.prefixMessage(player, noPermission)
            );
            return true;
        }

        if(!event.isCancelled()) {
            config = plugin.getConfig();
            player.teleport(spawn);
        }

        return true;
    }
}