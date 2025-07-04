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

package me.calrl.hubbly.commands.subcommands;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.interfaces.SubCommand;
import me.calrl.hubbly.utils.ChatUtils;
import me.calrl.hubbly.utils.MessageBuilder;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

import java.util.Map;

public class VersionCommand implements SubCommand {
    private final Hubbly plugin;
    private final FileConfiguration config;

    public VersionCommand(Hubbly plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }

    @Override
    public String getName() {
        return "version";
    }

    @Override
    public String getDescription() {
        return "Gets the version";
    }

    @Override
    public String getUsage() {
        return "/hubbly version";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(sender.hasPermission("hubbly.command.version")) {
            PluginDescriptionFile file = plugin.getDescription();
            sender.sendMessage(
                    ChatUtils.prefixMessage(plugin, "Version: " + file.getVersion())
            );
            sender.sendMessage(
                    ChatUtils.prefixMessage(plugin, "api-VERSION: " + file.getAPIVersion())
            );
            sender.sendMessage(
                    ChatUtils.prefixMessage(plugin, "Website: " + file.getWebsite())
            );

        } else {
            new MessageBuilder(plugin)
                    .setPlayer(sender)
                    .setKey("no_permission_command")
                    .send();
        }
    }
}
