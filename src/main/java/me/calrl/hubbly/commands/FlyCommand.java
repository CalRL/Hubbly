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

public class FlyCommand implements CommandExecutor {

    private final Hubbly plugin;
    private FileConfiguration config = Hubbly.getInstance().getConfig();
    private static final String FLY_METADATA_KEY = "hubbly.canFly";

    public FlyCommand(Hubbly plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        MessageBuilder builder = new MessageBuilder().setPlugin(plugin).setPlayer(sender);

        if (!(sender instanceof Player player)) {
            builder.setKey(LocaleKey.NO_CONSOLE).send();
            return true;
        }
        GameMode gameMode = player.getGameMode();
        if(gameMode == GameMode.SPECTATOR) return true;

        if (!config.getBoolean("player.fly.enabled")) {
            builder.setKey(LocaleKey.NO_FLY_ENABLED).send();
            return true;
        }

        if (!player.hasPermission(Permissions.COMMAND_FLY.getPermission()) && !player.isOp()) {
            builder.setKey(LocaleKey.NO_PERMISSION_COMMAND).send();
            return true;
        }

        PersistentDataContainer dataContainer = player.getPersistentDataContainer();

        Byte canFly = dataContainer.get(plugin.FLY_KEY, PersistentDataType.BYTE);

        if (canFly == null) {
            canFly = 0;
        }

        if (canFly == 1) {
            player.setFlying(false);
            builder.setKey(LocaleKey.FLY_DISABLE).send();
            //player.sendMessage(ChatUtils.prefixMessage(plugin, player, config.getString("messages.fly.disable")));
            dataContainer.set(plugin.FLY_KEY, PersistentDataType.BYTE, (byte) 0);
        } else {
            builder.setKey(LocaleKey.FLY_ENABLE).send();
            //player.sendMessage(ChatUtils.prefixMessage(plugin, player, config.getString("messages.fly.enable")));
            dataContainer.set(plugin.FLY_KEY, PersistentDataType.BYTE, (byte) 1);
        }

        return true;
    }
}
