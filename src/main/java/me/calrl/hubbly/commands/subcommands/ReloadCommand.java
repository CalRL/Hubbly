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
import me.calrl.hubbly.enums.Permissions;
import me.calrl.hubbly.managers.BossBarManager;
import me.calrl.hubbly.interfaces.SubCommand;
import me.calrl.hubbly.utils.MessageBuilder;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

public class ReloadCommand implements SubCommand {


    private FileConfiguration config = Hubbly.getInstance().getConfig();
    private final Hubbly plugin;
    private BossBarManager bossBarManager;

    public ReloadCommand(Hubbly plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public String getDescription() {
        return "Reloads the plugin's configuration";
    }

    @Override
    public String getUsage() {
        return "/hubbly reload";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!sender.hasPermission(Permissions.COMMAND_RELOAD.getPermission())) {
            new MessageBuilder(plugin)
                    .setPlayer(sender)
                    .setKey("no_permission_command")
                    .send();
            return;
        }
        try {
            bossBarManager = plugin.gameplay().bossBarManager();
            if (bossBarManager != null) {
                bossBarManager.removeAllBossBars();
            }
            plugin.reloadPlugin();

            new MessageBuilder(plugin)
                    .setPlayer(sender)
                    .setKey("reload")
                    .send();

            bossBarManager = plugin.gameplay().bossBarManager();
            bossBarManager.reAddAllBossBars();
        } catch (Exception e) {
            plugin.getLogger().info(String.valueOf(e));
        }
    }
}
