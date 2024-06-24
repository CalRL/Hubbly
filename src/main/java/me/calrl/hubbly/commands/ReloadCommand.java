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

package me.calrl.hubbly.commands;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.interfaces.SubCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class ReloadCommand implements SubCommand {


    private final Logger logger;
    private FileConfiguration config;
    private final JavaPlugin plugin;

    public ReloadCommand(Logger logger, FileConfiguration config, JavaPlugin plugin) {
        this.logger = logger;
        this.config = config;
        this.plugin = plugin;
    }
    @Override
    public void execute(Player player, String[] args) {
        Hubbly.getInstance().reloadConfiguration();
        player.sendMessage(config.getString("messages.reload"));

    }
}
