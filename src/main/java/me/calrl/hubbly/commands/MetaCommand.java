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
import org.bukkit.entity.Player;

public class MetaCommand implements SubCommand {
    private static final String FLY_METADATA_KEY = "hubbly.canFly";
    @Override
    public void execute(Player player, String[] args) {
        Hubbly.getInstance().getLogger().info(player.getMetadata(FLY_METADATA_KEY).toString());
    }
}
