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
import me.calrl.hubbly.enums.LocaleKey;
import me.calrl.hubbly.enums.Permissions;
import me.calrl.hubbly.enums.data.PlayerMovementMode;
import me.calrl.hubbly.handlers.PlayerMovementHandler;
import me.calrl.hubbly.utils.ChatUtils;
import me.calrl.hubbly.utils.MessageBuilder;
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

import java.util.ArrayList;
import java.util.List;

public class FlyCommand implements CommandExecutor {

    private final Hubbly plugin;
    private FileConfiguration config = Hubbly.getInstance().getConfig();

    public FlyCommand(Hubbly plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        MessageBuilder builder = new MessageBuilder().setPlugin(plugin).setPlayer(sender);

        if (!(sender instanceof Player player)) {
            builder.setKey("no_console").send();
            return true;
        }
        GameMode gameMode = player.getGameMode();
        if(gameMode == GameMode.SPECTATOR) return true;

        if (!config.getBoolean("player.fly.enabled")) {
            builder.setKey("no_fly_enabled").send();
            return true;
        }

        if (!player.hasPermission(Permissions.COMMAND_FLY.getPermission()) && !player.isOp()) {
            builder.setKey("no_permission_command").send();
            return true;
        }
        PlayerMovementHandler handler = new PlayerMovementHandler(player, plugin);
        PlayerMovementMode mode = handler.getMovementMode();

        if(mode == PlayerMovementMode.FLY) {
            handler.setMovementMode(PlayerMovementMode.NONE);
            builder.setKey("fly.disable");
        } else {
            handler.setMovementMode(PlayerMovementMode.FLY);
            builder.setKey("fly.enable");
        }

        builder.send();

        return true;
    }
}
