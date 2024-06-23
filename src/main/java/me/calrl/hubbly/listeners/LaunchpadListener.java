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

package me.calrl.hubbly.listeners;

import me.calrl.hubbly.Hubbly;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import java.util.logging.Logger;

public class LaunchpadListener implements Listener {

    private final Logger logger;
    private final Hubbly plugin;
    private final FileConfiguration config;

    public LaunchpadListener(Logger logger, Hubbly plugin, FileConfiguration config) {
        this.logger = logger;
        this.plugin = plugin;
        this.config = config;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Block blockStandingOn = player.getLocation().getBlock(); // Get the block the player is standing on
        Block blockBelow = player.getLocation().subtract(0, 1, 0).getBlock(); // Get the block directly below the player

        Material launchpadMaterial = Material.valueOf(config.getString("launchpad.type"));
        if (blockStandingOn.getType() == launchpadMaterial || blockBelow.getType() == launchpadMaterial) {
            if (player.hasPermission("hubbly.launchpad.use") || player.isOp()) {
                //logger.info("Player is on a launchpad!");

                // Get the player's current direction
                Vector direction = player.getLocation().getDirection();

                // Set the Y component of the direction to ensure upward launch
                direction.setY(0.5); // Adjust this value for the desired upward strength

                // Scale the direction vector for forward boost
                direction.multiply(2); // Adjust this value for the desired forward strength

                // Set the player's velocity to the new direction
                player.setVelocity(direction);
            }
        }
    }
}
