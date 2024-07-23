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

package me.calrl.hubbly.listeners.player;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.managers.cooldown.CooldownType;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.util.Vector;

import java.util.UUID;

public class DoubleJumpListener implements Listener {

    private static final String FLY_METADATA_KEY = "hubbly.canFly";
    @EventHandler
    public void onToggleFlight(PlayerToggleFlightEvent event) {
        if(Hubbly.getInstance().getDisabledWorldsManager().inDisabledWorld(event.getPlayer().getLocation())) return;
        FileConfiguration config = Hubbly.getInstance().getConfig();
        Player player = event.getPlayer();

        boolean canFly = player.getMetadata(FLY_METADATA_KEY).get(0).asBoolean();

            if (!canFly && config.getBoolean("double_jump.enabled")) {
                event.setCancelled(true);
                player.setFlying(false);
                UUID uuid = player.getUniqueId();
                if(!Hubbly.getInstance().getCooldownManager().tryCooldown(uuid, CooldownType.DOUBLE_JUMP, config.getLong("double_jump.cooldown")))  return;


                Vector direction = player.getLocation().getDirection();

                // Set the Y component of the direction to ensure upward launch
                direction.setY(config.getDouble("double_jump.power_y")); // Adjust this value for the desired upward strength

                // Scale the direction vector for forward boost
                direction.multiply(config.getDouble("double_jump.power", 1.0)); // Adjust this value for the desired forward strength

                // Set the player's velocity to the new direction

                player.setVelocity(direction);


            }
    }
}
