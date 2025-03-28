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
import me.calrl.hubbly.commands.subcommands.GiveCommand;
import me.calrl.hubbly.enums.LocaleKey;
import me.calrl.hubbly.interfaces.SubCommand;
import me.calrl.hubbly.managers.SubCommandManager;
import me.calrl.hubbly.utils.ChatUtils;
import me.calrl.hubbly.utils.MessageBuilder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.logging.Logger;

public class HubblyCommand implements TabExecutor {

    private final FileConfiguration config;

    private final Hubbly plugin;
    private final SubCommandManager subCommandManager;
    private Map<String, SubCommand> subCommands;
    public HubblyCommand(Hubbly plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        this.subCommandManager = plugin.getSubCommandManager();
        this.subCommands = subCommandManager.getSubCommands();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        // If no arguments are provided, show usage message
        MessageBuilder builder = new MessageBuilder(plugin)
                .setPlayer(sender);
        if (args.length == 0) {
            builder.setKey("usage.hubbly")
                    .send();
            return true;
        }

        // Get the subcommand
        String subCommandName = args[0].toLowerCase();
        SubCommand subCommand = subCommands.get(subCommandName);

        // If subcommand exists, execute it
        if (subCommand != null) {
            subCommand.execute(sender, args);
        } else {
            builder.setKey("unknown_command")
                    .send();
        }

        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if(!(sender instanceof Player player)) return null;

        final List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            StringUtil.copyPartialMatches(args[0], subCommands.keySet(), completions);
        } else if (args.length > 1) {
            SubCommand subCommand = subCommands.get(args[0].toLowerCase());
            if (subCommand != null) {
                return subCommand.tabComplete(player, Arrays.copyOfRange(args, 1, args.length));
            }
        }
        return completions;
    }
}

