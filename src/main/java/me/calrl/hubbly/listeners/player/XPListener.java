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
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Year;

public class XPListener implements Listener {

    private final Hubbly plugin;
    public XPListener(Hubbly plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if(inDisabledWorld(player)) return;

        FileConfiguration config = plugin.getConfig();
        int level = (int) config.getDouble("player.experience.level", 0);
        player.setLevel(level);
        float experiencePercentage = this.getExperienceFromConfig(config);

        player.setExp(experiencePercentage);

    }

    private float getExperienceFromConfig(FileConfiguration config) {
        float experienceFloat;

        Object configPercentage = config.get("player.experience.progress", 0.5f);

        if(configPercentage.equals("YEAR")) {
            experienceFloat = this.getElapsedPercentage();
        } else {
            if(configPercentage instanceof Number number) {
                experienceFloat = number.floatValue();
            } else {
                throw new IllegalArgumentException("Invalid percentage value: " + configPercentage);
            }
        experienceFloat = validateExperience(experienceFloat);
        }
        return experienceFloat;
    }
    private float validateExperience(float experience) {
        if(experience > 1.0f) {
            experience = 0.5f;
            plugin.getLogger().warning("Experience 'progress' in config is over 1.0");
        } else if(experience < 0.0f) {
            experience = 0.5f;
            plugin.getLogger().warning("Experience 'progress' in config is under 0.0");
        }
        return experience;
    }

    @EventHandler
    private void onPlayerXP(PlayerExpChangeEvent event) {
        Player player = event.getPlayer();

        if(inDisabledWorld(player)) return;
        FileConfiguration config = plugin.getConfig();
        if(config.getBoolean("player.experience.enabled")) {
            event.setAmount(0);
        }
    }

    private boolean inDisabledWorld(Player player) {
        World world = player.getWorld();
        DisabledWorlds disabledWorlds = plugin.getDisabledWorldsManager();
        if(disabledWorlds.inDisabledWorld(world)) {
            return true;
        }
        return false;
    }


    public float getElapsedPercentage() {
        LocalDateTime now = LocalDateTime.now(); // Current local date & time
        Year year = this.getYear();

        LocalDateTime start = LocalDateTime.of(year.getValue(), 1, 1, 0, 0, 0);
        LocalDateTime end = LocalDateTime.of(year.getValue(), 12, 31, 23, 59, 59);

        long elapsedMillis = Duration.between(start, now).toMillis();
        long totalMillis = Duration.between(start, end).toMillis();

        return (float) elapsedMillis / totalMillis;
    }

    public Year getYear() {
        return Year.now();
    }
}
