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

import me.calrl.hubbly.Hubbly;
import org.bukkit.NamespacedKey;

public enum PluginKeys {

    PLAYER_VISIBILITY(new NamespacedKey(Hubbly.getInstance(), "player_visibility")),
    SOCIALS(new NamespacedKey(Hubbly.getInstance(), "socials")),
    SELECTOR(new NamespacedKey(Hubbly.getInstance(), "selector")),
    TRIDENT(new NamespacedKey(Hubbly.getInstance(), "trident")),
    GRAPPLING_HOOK(new NamespacedKey(Hubbly.getInstance(), "grappling_hook")),
    AOTE(new NamespacedKey(Hubbly.getInstance(), "aote")),
    ENDER_BOW(new NamespacedKey(Hubbly.getInstance(),"ender_bow")),
    ACTIONS_KEY(new NamespacedKey(Hubbly.getInstance(), "customActions")),
    LAUNCHPAD_KEY(new NamespacedKey(Hubbly.getInstance(), "launchpad"));


    private final NamespacedKey key;
    PluginKeys(NamespacedKey key) {
        this.key = key;
    }

    public NamespacedKey getKey() {
        return key;
    }

}
