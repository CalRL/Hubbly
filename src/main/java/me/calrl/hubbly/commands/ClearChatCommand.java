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
import me.calrl.hubbly.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ClearChatCommand implements CommandExecutor {

    public final int CHAT_CLEAR_MESSAGE_COUNT = 100;

    private final Hubbly plugin;
    public ClearChatCommand(Hubbly plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(!(sender instanceof Player player)) return true;
        if(!player.hasPermission("hubbly.command.clearchat")) {
            FileConfiguration config = plugin.getConfig();
            String noPermission = config.getString("messages.no_permission_command");
            player.sendMessage(
                    ChatUtils.prefixMessage(plugin, player, noPermission)
            );
            return true;
        }

        for(int i = 0; i <= CHAT_CLEAR_MESSAGE_COUNT; i++) {
            for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                onlinePlayer.sendMessage("");
            }
        }

        plugin.getDebugMode().info("Chat cleared by " + player.getName());


        return true;
    }
}
