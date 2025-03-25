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
import me.calrl.hubbly.functions.AngleRounder;
import me.calrl.hubbly.utils.ChatUtils;
import me.calrl.hubbly.utils.MessageBuilder;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SetSpawnCommand implements CommandExecutor {

    private final Hubbly plugin;
    private FileConfiguration config;

    public SetSpawnCommand(Hubbly plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }

    public double spawnRound(double value) {
        if (value == 0.5 || value == -0.5) {
            return value;
        } else {
            return Math.round(value * 2) / 2.0;
        }
    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(!(sender instanceof Player player)) return true;
        if (!sender.hasPermission("hubbly.command.setspawn")) {
            new MessageBuilder(plugin)
                    .setPlayer(player)
                    .setKey(LocaleKey.NO_PERMISSION_COMMAND)
                    .send();
            return true;
        }

        Location location = player.getLocation();

        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();
        float yaw = location.getYaw();
        float pitch = location.getPitch();
        float roundedYaw = new AngleRounder(yaw).getRoundedAngle();
        float roundedPitch = new AngleRounder(pitch).getRoundedAngle();

        World world = location.getWorld();
        if(world == null) {
            player.sendMessage(
                    ChatUtils.prefixMessage(
                            plugin,
                            player,
                            config.getString("messages.console_error")
                    )
            );
            throw new NullPointerException("World is null?");
        }

        config.set("spawn.world", world.getName());
        config.set("spawn.x", spawnRound(x));
        config.set("spawn.y", spawnRound(y));
        config.set("spawn.z", spawnRound(z));
        config.set("spawn.yaw", roundedYaw);
        config.set("spawn.pitch", roundedPitch);

        plugin.saveConfig();

        new MessageBuilder(plugin).setPlayer(player).setKey(LocaleKey.SUCCESS).send();

        return true;
    }
}