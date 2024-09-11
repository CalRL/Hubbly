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
package me.calrl.hubbly.action.actions;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.action.Action;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class LaunchAction implements Action {
    @Override
    public String getIdentifier() {
        return "LAUNCH";
    }

    @Override
    public void execute(Hubbly plugin, Player player, String data) {
        new BukkitRunnable() {

            private final double powerY = plugin.getConfig().getDouble("launchpad.power_y");
            private final double power = plugin.getConfig().getDouble("launchpad.power");
            @Override
            public void run() {
                Vector direction = player.getLocation().getDirection();

                direction.setY(powerY);

                direction.multiply(power);

                player.setVelocity(direction);
            }
        }.runTask(plugin);
    }
}
