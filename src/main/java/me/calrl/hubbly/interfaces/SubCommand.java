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

package me.calrl.hubbly.interfaces;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public interface SubCommand {
    String getName();

    String getDescription();

    String getUsage();

    void execute(CommandSender sender, String[] args);

    /**
     * Provides tab completion suggestions for the subcommand (Players only).
     *
     * @param player the player requesting tab completion
     * @param args   the current command arguments
     * @return a list of possible completions
     */
    default List<String> tabComplete(Player player, String[] args) {
        return Collections.emptyList();
    }
}