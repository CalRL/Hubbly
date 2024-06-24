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
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.Objects;
import java.util.logging.Logger;

public class VoidDamageListener implements Listener {

    private FileConfiguration config = Hubbly.getInstance().getConfig();
    private final Logger logger;
    public VoidDamageListener(Logger logger) {
        this.logger = logger;
    }

    @EventHandler
    private void onEntityDamage(EntityDamageEvent event) {
        if(event.getEntity() instanceof Player && config.getBoolean("antivoid.enabled")) {
            if(event.getCause() == EntityDamageEvent.DamageCause.VOID) {
                event.setCancelled(true);
                String worldName = config.getString("spawn.world");
                World world = Bukkit.getWorld(worldName);
                double x = config.getDouble("spawn.x");
                double y = config.getDouble("spawn.y");
                double z = config.getDouble("spawn.z");
                float yaw = (float) config.getDouble("spawn.yaw");
                float pitch = (float) config.getDouble("spawn.pitch");
                event.getEntity().teleport(new Location(world, x, y, z, yaw, pitch));

            }
        }
    }
}
