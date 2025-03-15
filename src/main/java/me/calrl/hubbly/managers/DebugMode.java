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
package me.calrl.hubbly.managers;

import me.calrl.hubbly.Hubbly;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.logging.Level;
import java.util.logging.Logger;

public class DebugMode {
    private final FileConfiguration config = Hubbly.getInstance().getConfig();
    private final Logger logger = Hubbly.getInstance().getLogger();

    public void info(String message) {
        if(config.getBoolean("debug")){
            logger.log(Level.INFO, "[DEBUG] " + message);
        }
    }
    public void warn(String message) {
        if(config.getBoolean("debug")){
            logger.log(Level.WARNING, "[DEBUG] " + message);
        }
    }

    public void severe(String message) {
        if(config.getBoolean("debug")){
            logger.log(Level.SEVERE, "[DEBUG] " + message);
        }
    }

}
