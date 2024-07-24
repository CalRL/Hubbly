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
import me.calrl.hubbly.utils.ChatUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.logging.Logger;

public class FlyCommand implements CommandExecutor {

    private final Logger logger;
    private FileConfiguration config = Hubbly.getInstance().getConfig();
    private static final String FLY_METADATA_KEY = "hubbly.canFly";

    public FlyCommand(Logger logger) {
        this.logger = logger;
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;

        if (!config.getBoolean("player.fly.enabled")) {
            return true;
        }

        if(!player.getAllowFlight()) {
            player.setAllowFlight(true);
        }

        if (!player.hasPermission("hubbly.command.fly") && !player.isOp()) {
            player.sendMessage(ChatUtils.translateHexColorCodes("messages.no_permission"));
            return true;
        }

        boolean canFly = false;
        if (player.hasMetadata(FLY_METADATA_KEY)) {
            canFly = player.getMetadata(FLY_METADATA_KEY).get(0).asBoolean();
        } else {
            player.setMetadata(FLY_METADATA_KEY, new FixedMetadataValue(Hubbly.getInstance(), false));
        }

        if (canFly) {
            player.setMetadata(FLY_METADATA_KEY, new FixedMetadataValue(Hubbly.getInstance(), false));
            player.setFlying(false);
            player.sendMessage(ChatUtils.translateHexColorCodes(Objects.requireNonNull(config.getString("messages.fly.disable"))));
        } else {
            player.setMetadata(FLY_METADATA_KEY, new FixedMetadataValue(Hubbly.getInstance(), true));
            player.sendMessage(ChatUtils.translateHexColorCodes(Objects.requireNonNull(config.getString("messages.fly.enable"))));
        }

        return true;
    }
}
