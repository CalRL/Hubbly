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
import me.calrl.hubbly.functions.BossBarManager;
import me.calrl.hubbly.interfaces.SubCommand;
import me.calrl.hubbly.listeners.player.PlayerJoinListener;
import me.calrl.hubbly.utils.ChatUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.logging.Logger;

public class ReloadCommand implements SubCommand {


    private FileConfiguration config = Hubbly.getInstance().getConfig();
    private final Hubbly plugin;

    public ReloadCommand(Hubbly plugin) {
        this.plugin = plugin;
    }

    public String getIdentifier() {
        return "RELOAD";
    }
    @Override
    public void execute(Player player, String[] args) {
        if(player.hasPermission(Permissions.COMMAND_RELOAD.getPermission())) {

            try {
                BossBarManager bossBarManager = BossBarManager.getInstance();
                if (bossBarManager != null) {
                    bossBarManager.removeAllBossBars();
                }
                plugin.reloadPlugin();
                player.sendMessage(
                        ChatUtils.prefixMessage(player, config.getString("messages.reload", "Config reloaded."))
                );
                BossBarManager.initialize(Hubbly.getInstance().getConfig());
                bossBarManager = BossBarManager.getInstance();
                bossBarManager.reAddAllBossBars();
            } catch (Exception e) {
                plugin.getLogger().info(String.valueOf(e));
            }

        }
    }
}
