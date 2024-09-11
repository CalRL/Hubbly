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

package me.calrl.hubbly.listeners.world;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.enums.Permissions;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

public class AntiWDL implements PluginMessageListener {
    private final Hubbly plugin;
    public AntiWDL(Hubbly plugin) {
        this.plugin = plugin;
    }
    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, @NotNull byte[] bytes) {
        FileConfiguration config = plugin.getConfig();
        if(player.hasPermission(Permissions.BYPASS_ANTI_WDL.getPermission())) {
            if(!channel.equals("wdl:init")) return;
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeInt(0);
            out.writeBoolean(false);

            player.sendPluginMessage(plugin, "wdl:control", out.toByteArray());

            for(Player p : Bukkit.getOnlinePlayers()) {
                if(p.hasPermission(Permissions.NOTIFY_WDL.getPermission())) {
                    player.sendMessage(player.getName() + " tried to download the world!");
                }
            }
            Bukkit.getLogger().info(player.getName() + " tried to download the world");
        } else {
            player.sendMessage(config.getString("messages.no_permission", "No permission."));
        }
    }
}
