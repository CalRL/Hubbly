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
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class VersionCommand implements SubCommand {
    private final Hubbly plugin;
    private final FileConfiguration config;

    public VersionCommand(Hubbly plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }


    @Override
    public String getIdentifier() {
        return "VERSION";
    }

    @Override
    public void execute(Player player, String[] args) {
        if(player.hasPermission("hubbly.command.version") || player.isOp()) {

            player.sendMessage(
                    ChatUtils.prefixMessage(player, "<#FF00FF>Version: " + plugin.getDescription().getVersion())
            );
            player.sendMessage(
                    ChatUtils.prefixMessage(player, "api-VERSION: " + plugin.getDescription().getAPIVersion())
            );
            player.sendMessage(
                    ChatUtils.prefixMessage(player, "Website: " + plugin.getDescription().getWebsite())
            );
        } else {
            player.sendMessage(config.getString("messages.no_permission_command", "No permission"));
        }

    }
}
