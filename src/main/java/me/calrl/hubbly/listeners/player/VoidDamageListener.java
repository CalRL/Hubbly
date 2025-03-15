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
import me.calrl.hubbly.managers.DisabledWorlds;
import me.calrl.hubbly.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class VoidDamageListener implements Listener {

    private FileConfiguration config;
    private Hubbly plugin;
    public VoidDamageListener(Hubbly plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    private void onEntityDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Player player)) return;

        Location playerLocation = player.getLocation();
        DisabledWorlds disabledWorlds = plugin.getDisabledWorldsManager();
        if(disabledWorlds.inDisabledWorld(playerLocation)) return;

        GameMode gameMode = player.getGameMode();
        if(gameMode == GameMode.SPECTATOR) return;

        this.config = plugin.getConfig();
        boolean isEnabled = config.getBoolean("antivoid.enabled");
        if(isEnabled) {
            DamageCause damageCause = event.getCause();
            if(damageCause == DamageCause.VOID) {
                plugin.getDebugMode().info(player.getName() + " was hit by the void.. teleporting..");

                Utils utils = plugin.getUtils();
                Location spawn = utils.getSpawn();

                player.setVelocity(player.getVelocity().setY(0));
                player.setFallDistance(0f);

                Bukkit.getScheduler().runTaskLater(
                        plugin,
                        () -> player.teleport(spawn),
                        1L
                );

                event.setCancelled(true);
            }
        }
    }
}
