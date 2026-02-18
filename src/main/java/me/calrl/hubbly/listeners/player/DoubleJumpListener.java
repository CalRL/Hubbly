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
import me.calrl.hubbly.enums.PluginKeys;
import me.calrl.hubbly.enums.data.PlayerMovementMode;
import me.calrl.hubbly.handlers.PlayerMovementHandler;
import me.calrl.hubbly.managers.cooldown.CooldownType;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.UUID;

public class DoubleJumpListener implements Listener {
    private final Hubbly plugin;

    public DoubleJumpListener(Hubbly plugin) {
        this.plugin = plugin;
    }

    private static final String FLY_METADATA_KEY = "hubbly.canFly";
    @EventHandler
    public void onToggleFlight(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();

        if(plugin.services().disabledWorlds().inDisabledWorld(player.getLocation())) {
            return;
        }

        if(player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) return;
        FileConfiguration config = this.plugin.getConfig();

        PersistentDataContainer dataContainer = player.getPersistentDataContainer();
        if(!dataContainer.has(PluginKeys.MOVEMENT_KEY.getKey())) return;

        if(config.getBoolean("double_jump.enabled")) {
            new PlayerMovementHandler(player, plugin).handleMovement(event);
        }

    }
    @EventHandler
    private void onGamemodeChange(PlayerGameModeChangeEvent event) {
        Player player = event.getPlayer();
        if(plugin.services().disabledWorlds().inDisabledWorld(player.getLocation())) return;
        if(event.getNewGameMode() == GameMode.ADVENTURE || event.getNewGameMode() == GameMode.SURVIVAL) {
            FileConfiguration config = plugin.getConfig();
            if(config.getBoolean("double_jump.enabled")) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        player.setAllowFlight(true);
                    }
                }.runTaskLater(plugin, 1L);

            }
        }
    }
}