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
import me.calrl.hubbly.interfaces.SubCommand;
import me.calrl.hubbly.utils.ChatUtils;
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

    private Logger logger;
    private FileConfiguration config = Hubbly.getInstance().getConfig();

    private final Hubbly plugin;
    private final Map<String, SubCommand> subCommands = new HashMap<>();
    public HubblyCommand(Hubbly plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        registerSubCommands();
    }

    private void registerSubCommands() {
        subCommands.put("reload", new ReloadCommand(plugin));
        subCommands.put("selector", new SelectorCommand(plugin));
        subCommands.put("version", new VersionCommand(plugin));
        subCommands.put("nextannouncement", new NextAnnouncementCommand(plugin));
        subCommands.put("give", new GiveCommand(plugin));
        subCommands.put("saveitem", new SaveItemCommand(plugin));
        subCommands.put("loaditem", new LoadItemCommand(plugin));
    }



    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        // If no arguments are provided, show usage message
        if (args.length == 0) {
            sender.sendMessage(ChatUtils.prefixMessage(plugin, "Usage: /hubbly <command> <args>"));
            return true;
        }

        // Get the subcommand
        String subCommandName = args[0].toLowerCase();
        SubCommand subCommand = subCommands.get(subCommandName);

        // If subcommand exists, execute it
        if (subCommand != null) {
            subCommand.execute(sender, args);
        } else {
            sender.sendMessage(ChatUtils.prefixMessage(plugin, "Unknown command. Use '/hubbly help' for a list of commands."));
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

