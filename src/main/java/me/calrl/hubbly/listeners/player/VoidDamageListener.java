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
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.Objects;
import java.util.logging.Logger;

public class VoidDamageListener implements Listener {

    private FileConfiguration config = Hubbly.getInstance().getConfig();
    private Hubbly plugin;
    public VoidDamageListener(Hubbly plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    private void onEntityDamage(EntityDamageEvent event) {
        if(Hubbly.getInstance().getDisabledWorldsManager().inDisabledWorld(event.getEntity().getLocation())) return;
        Entity entity = event.getEntity();
        if (!(entity instanceof Player player)) return;

        if(plugin.getDisabledWorldsManager().inDisabledWorld(player.getLocation())) return;

        GameMode gameMode = player.getGameMode();
        if(gameMode != GameMode.SURVIVAL && gameMode != GameMode.ADVENTURE) return;

        if(event.getEntity() instanceof Player && config.getBoolean("antivoid.enabled")) {
            if(event.getCause() == EntityDamageEvent.DamageCause.VOID) {
                event.setCancelled(true);

                event.getEntity().teleport(plugin.getUtils().getSpawn());

            }
        }
    }
}
