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
import me.calrl.hubbly.interfaces.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class HubblyCommand implements CommandExecutor {

    private Logger logger;
    private final FileConfiguration config;
    private final JavaPlugin plugin;
    private final Map<String, SubCommand> subCommands = new HashMap<>();

    public HubblyCommand(Logger logger, FileConfiguration config, Hubbly plugin) {
        this.logger = logger;
        this.config = config;
        this.plugin = plugin;
        registerSubCommands();
    }

    private void registerSubCommands() {
        subCommands.put("give", new GiveCommand(plugin, config));
        subCommands.put("reload", new ReloadCommand(logger, config, plugin));
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String String, @NotNull String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(config.getString("messages.no_console"));
            return true;
        }
        Player player = (Player) sender;

        if(args.length == 0) {
            player.sendMessage(ChatColor.YELLOW + "Usage: /hubbly <command> <args>");
            return true;
        }
        SubCommand subCommand = subCommands.get(args[0].toLowerCase());
        if(subCommand != null) {
            subCommand.execute(player, args);
        } else {
            player.sendMessage(ChatColor.YELLOW + "Unknown command.");
        }

        return true;
    }



}
