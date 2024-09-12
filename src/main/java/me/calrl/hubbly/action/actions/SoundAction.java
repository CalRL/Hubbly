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
package me.calrl.hubbly.action.actions;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.action.Action;
import me.calrl.hubbly.managers.DebugMode;
import org.bukkit.entity.Player;

public class SoundAction implements Action {
    @Override
    public String getIdentifier() {
        return "SOUND";
    }

    @Override
    public void execute(Hubbly plugin, Player player, String data) {
        DebugMode debugMode = plugin.getDebugMode();
        try {
            player.playSound(player.getLocation(), data, 1L, 1L);
        } catch (Exception e) {
            debugMode.severe("Sound action failed: " + e);
        }

    }
}
