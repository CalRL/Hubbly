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
import me.calrl.hubbly.commands.subcommands.*;
import me.calrl.hubbly.interfaces.SubCommand;
import me.calrl.hubbly.utils.ChatUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.logging.Logger;

public class HubblyCommand implements TabExecutor {

    private Logger logger;
    private FileConfiguration config = Hubbly.getInstance().getConfig();

    private final Hubbly plugin;
    private final Map<String, SubCommand> subCommands = new HashMap<>();
    private final String[] commands = {"reload", "version", "selector", "nextannouncement"};
    public HubblyCommand(Logger logger, Hubbly plugin) {
        this.logger = logger;
        this.plugin = plugin;
        registerSubCommands();

    }

    private void registerSubCommands() {
        subCommands.put("reload", new ReloadCommand(logger, plugin));
        subCommands.put("selector", new SelectorCommand(plugin));
        subCommands.put("version", new VersionCommand(plugin));
        subCommands.put("nextannouncement", new NextAnnouncementCommand(plugin));
        subCommands.put("getlockchat", new GetLockChatCommand(plugin));
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String String, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatUtils.translateHexColorCodes(config.getString("messages.no_console")));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(ChatColor.YELLOW + "Usage: /hubbly <command> <args>");
            return true;
        }
        SubCommand subCommand = subCommands.get(args[0].toLowerCase());
        if (subCommand != null) {
            subCommand.execute(player, args);
        } else {
            sender.sendMessage(ChatColor.YELLOW + "Unknown command.");
        }


        return true;
    }



    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        final List<String> completions = new ArrayList<>();
        StringUtil.copyPartialMatches(args[0], List.of(commands), completions);
        return completions;
    }
}
