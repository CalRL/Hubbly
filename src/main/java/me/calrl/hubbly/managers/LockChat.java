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
import org.bukkit.configuration.file.FileConfiguration;

public class LockChat {
    private final Hubbly plugin;
    private boolean isChatLocked;
    public LockChat(Hubbly plugin) {
        this.plugin = plugin;
        isChatLocked = plugin.getConfig().getBoolean("lock_chat");
        String msg = isChatLocked ? "Chat is LOCKED" : "Chat is UNLOCKED";
        new DebugMode().info(msg);
    }
    public void flipChatLock() {
        isChatLocked = !isChatLocked;
        saveState();
    }

    public boolean getChatLock() {
        return isChatLocked;
    }


    private void saveState() {
        FileConfiguration config = plugin.getConfig();
        config.set("lock_chat", getChatLock());
        plugin.saveConfig();
    }
}
