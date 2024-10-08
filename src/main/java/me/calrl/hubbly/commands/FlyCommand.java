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
import me.calrl.hubbly.utils.ChatUtils;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class FlyCommand implements CommandExecutor {

    private final Hubbly plugin;
    private FileConfiguration config = Hubbly.getInstance().getConfig();
    private static final String FLY_METADATA_KEY = "hubbly.canFly";

    public FlyCommand(Hubbly plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players.");
            return true;
        }
        GameMode gameMode = player.getGameMode();
        if(gameMode == GameMode.SPECTATOR) return true;

        if (!config.getBoolean("player.fly.enabled")) {
            player.sendMessage(config.getString("messages.no_fly_enabled", "Flight is disabled in config"));
            return true;
        }

        if (!player.hasPermission(Permissions.COMMAND_FLY.getPermission()) && !player.isOp()) {
            player.sendMessage(
                    ChatUtils.prefixMessage(player, config.getString("messages.no_permission_command"))
            );
            return true;
        }

        PersistentDataContainer dataContainer = player.getPersistentDataContainer();

        Byte canFly = dataContainer.get(plugin.FLY_KEY, PersistentDataType.BYTE);

        if (canFly == null) {
            canFly = 0;
        }

        if (canFly == 1) {
            player.setFlying(false);
            player.sendMessage(ChatUtils.prefixMessage(player, config.getString("messages.fly.disable")));
            dataContainer.set(plugin.FLY_KEY, PersistentDataType.BYTE, (byte) 0);
        } else {
            player.sendMessage(ChatUtils.prefixMessage(player, config.getString("messages.fly.enable")));
            dataContainer.set(plugin.FLY_KEY, PersistentDataType.BYTE, (byte) 1);
        }

        return true;
    }
}
