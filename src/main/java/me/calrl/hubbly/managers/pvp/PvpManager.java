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

package me.calrl.hubbly.managers.pvp;

import me.calrl.hubbly.Hubbly;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import java.util.List;

public class PvpManager {
    private List<String> players;
    private String PVP_METADATA_KEY = "hubbly.canPvp";

    public void setPvpMode(Player player) {
        player.setMetadata(PVP_METADATA_KEY, new FixedMetadataValue(Hubbly.getInstance(), true));
        players.add(player.getName());
    }
    public void disablePvpMode(Player player) {
        player.setMetadata(PVP_METADATA_KEY, new FixedMetadataValue(Hubbly.getInstance(), false));
        players.remove(player.getName());
    }

    public Boolean getPvpMode(Player player) {
        return player.getMetadata(PVP_METADATA_KEY).get(0).asBoolean();
    }
}
