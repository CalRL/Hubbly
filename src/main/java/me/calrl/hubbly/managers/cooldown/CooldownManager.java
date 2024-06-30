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

package me.calrl.hubbly.managers.cooldown;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import java.util.UUID;

public class CooldownManager {

    private Table<String, CooldownType, Long> cooldowns = HashBasedTable.create();

    public long getCooldown(UUID uuid, CooldownType type) {
        return calculateRemainder(cooldowns.get(uuid.toString(), type));
    }

    public long setCooldown(UUID uuid, CooldownType type, Long time) {
        return calculateRemainder(cooldowns.put(uuid.toString(), type, System.currentTimeMillis() + (time * 1000)));
    }

    public boolean tryCooldown(UUID uuid, CooldownType key, long delay) {
        if (getCooldown(uuid, key) / 1000 > 0) return false;
        setCooldown(uuid, key, delay + 1);
        return true;
    }

    private long calculateRemainder(Long expireTime) {
        return expireTime != null ? expireTime - System.currentTimeMillis() : Long.MIN_VALUE;
    }

}
