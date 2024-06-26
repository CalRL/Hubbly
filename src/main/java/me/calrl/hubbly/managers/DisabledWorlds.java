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
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class DisabledWorlds {

    private List<String> disabledWorlds;
    public DisabledWorlds() {
        setDisabledWorlds();
    }
    public boolean inDisabledWorld(World world) {
        return disabledWorlds != null && disabledWorlds.contains(world.getName());
    }

    public boolean inDisabledWorld(Location location) {
        return disabledWorlds != null && disabledWorlds.contains(location.getWorld().getName());
    }
    public void setDisabledWorlds() {
        FileConfiguration config = Hubbly.getInstance().getConfig();
        disabledWorlds = config.getStringList("disabled-worlds");
    }

    public String getDisabledWorlds() {
        return String.join(", ", disabledWorlds);
    }
}
