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

package me.calrl.hubbly.listeners.chat;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.managers.DebugMode;
import me.calrl.hubbly.utils.ChatUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.List;
import java.util.logging.Logger;

public class CommandBlockerListener implements Listener {

    private final Hubbly plugin;
    private List<String> blockedCommands;
    private Logger logger;
    private final DebugMode debugMode;
    public CommandBlockerListener(Hubbly plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.debugMode = plugin.getDebugMode();
    }
    @EventHandler
    private void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        if(event.isCancelled()) return;

        if(plugin.getDisabledWorldsManager().inDisabledWorld(event.getPlayer().getWorld())) return;
        String message = event.getMessage().toLowerCase();
        FileConfiguration config = plugin.getConfig();
        blockedCommands = config.getStringList("blocked_commands");
        Player player = event.getPlayer();
        for (String command : blockedCommands) {
            if(message.startsWith("/" + command.toLowerCase()) && (!player.hasPermission("hubbly.bypass.blockedcommands") && !player.isOp())) {
                event.setCancelled(true);
                message = config.getString("messages.blocked_command", "Unknown command %command%").replace("%command%", command);
                player.sendMessage(ChatUtils.translateHexColorCodes(message));
                debugMode.info(player.getName() + " tried to use /" + command);
                return;
            }
        }
    }
}
