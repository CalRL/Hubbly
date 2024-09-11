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
package me.calrl.hubbly.enums;

import org.bukkit.Sound;

public enum TridentSounds {
    RIPTIDE_1(Sound.ITEM_TRIDENT_RIPTIDE_1),
    RIPTIDE_2(Sound.ITEM_TRIDENT_RIPTIDE_2),
    RIPTIDE_3(Sound.ITEM_TRIDENT_RIPTIDE_3);

    private final Sound sound;
    TridentSounds(Sound sound) {
        this.sound = sound;
    }
    public Sound getSound() {
        return sound;
    }
}
