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

import java.util.ArrayList;
import java.util.List;

public class DisabledWorlds {

    private final List<World> disabledWorlds = new ArrayList<>();
    private final Hubbly plugin;
    public DisabledWorlds(Hubbly plugin) {
        this.plugin = plugin;
        this.setDisabledWorlds();
    }
    public boolean inDisabledWorld(World world) {
        return !disabledWorlds.isEmpty() && disabledWorlds.contains(world);
    }

    public boolean inDisabledWorld(Location location) {
        World world = location.getWorld();
        return this.inDisabledWorld(world);
    }

    public void setDisabledWorlds() {
        List<String> disabledWorldsList = this.getConfigWorldList();
        DebugMode debugMode = new DebugMode();
        if(disabledWorldsList.isEmpty()) {
            debugMode.info("No worlds to register");
        }

        for(String worldName : disabledWorldsList) {
            boolean worldExists = this.isWorldValid(worldName);
            if(!worldExists) {
                String errorMessage = String.format("World not found: %s", worldName);
                plugin.getLogger().warning(errorMessage);
            } else {
                World world = Bukkit.getWorld(worldName);
                disabledWorlds.add(world);
                debugMode.info("Registered Disabled World: " + worldName);
            }
        }

    }

    private boolean isWorldValid(String worldName) {
        World world = Bukkit.getWorld(worldName);
        return world != null;
    }

    private List<String> getConfigWorldList() {
        FileConfiguration config = plugin.getConfig();
        if(!config.isSet("disabled-worlds")) {
            return new ArrayList<>();
        }

        List<String> disabledWorldsList = config.getStringList("disabled-worlds");

        boolean isInverted = config.getBoolean("invert", false);
        if (isInverted) {
            disabledWorldsList = this.getInvertedWorlds();
        }

        return disabledWorldsList;
    }

    private List<String> getInvertedWorlds() {
        List<World> allWorlds = Bukkit.getWorlds();
        List<String> invertedWorlds = new ArrayList<>();

        for (World world : allWorlds) {
            if (!disabledWorlds.contains(world)) {
                invertedWorlds.add(world.getName());
            }
        }
        return invertedWorlds;
    }


    public List<World> getDisabledWorlds() {
        return this.disabledWorlds;
    }

    public void addDisabledWorld(World world) {
        if(disabledWorlds.contains(world)) {
            String message = String.format("World %s is already in the disabled worlds list!", world.getName());
            throw new IllegalArgumentException(message);
        }
        disabledWorlds.add(world);
    }

    public void reload() {
        disabledWorlds.clear();
        this.setDisabledWorlds();
    }
}
