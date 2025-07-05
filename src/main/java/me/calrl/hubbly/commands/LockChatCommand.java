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
import me.calrl.hubbly.enums.Permissions;
import me.calrl.hubbly.managers.DisabledWorlds;
import me.calrl.hubbly.managers.LockChat;
import me.calrl.hubbly.utils.ChatUtils;
import me.calrl.hubbly.utils.MessageBuilder;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class LockChatCommand implements CommandExecutor {

    private Hubbly plugin;
    private LockChat lockChat;
    public LockChatCommand(Hubbly plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(!commandSender.hasPermission(Permissions.COMMAND_LOCK_CHAT.getPermission())) return true;
        lockChat = plugin.getLockChat();
        String key;
        if(lockChat.getChatLock()) {
            key = "chat_unlocked";
            String message = plugin.getConfig().getString(key, "Chat has been unlocked by:");
            if(message.contains("%player%")) {
                message = message.replace("%player%", commandSender.getName());
            }
        } else {
            key = "chat_locked";
            String message = plugin.getConfig().getString(key, "Chat has been locked by:");
            if(message.contains("%player%")) {
                message = message.replace("%player%", commandSender.getName());
            }
        }
        DisabledWorlds disabledWorlds = plugin.getDisabledWorldsManager();
        MessageBuilder builder = new MessageBuilder(plugin)
                .setKey(key)
                .replace("%player_name%", commandSender.getName());


        for(Player p : Bukkit.getOnlinePlayers()) {
            if(disabledWorlds.inDisabledWorld(p.getWorld())) {
                continue;
            }
            builder.setPlayer(p).send();
        }

        lockChat.flipChatLock();

        return true;
    }
}
