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
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


public class ChatListener implements Listener {
    private final Hubbly plugin;
    private final List<String> blockedWords;
    private final DebugMode debugMode;
    public ChatListener(Hubbly plugin) {
        this.plugin = plugin;
        blockedWords = plugin.getConfig().getStringList("blocked_words.words").stream().map(String::toLowerCase).collect(Collectors.toList());
        debugMode = plugin.getDebugMode();
    }

    @EventHandler
    private void onPlayerChat(AsyncPlayerChatEvent event) {
        FileConfiguration config = plugin.getConfig();
        if(!config.getBoolean("blocked_words.enabled")) return;
        if(plugin.getDisabledWorldsManager().inDisabledWorld(event.getPlayer().getWorld())) return;

        boolean messageModified = false;
        String message = event.getMessage().toLowerCase();
        String method = config.getString("blocked_words.method");

        for(String word : blockedWords) {
            if(!message.contains(word)) return;
            if(method == null) {
                debugMode.info("blocked_words.method is null in config");
                return;
            }
            if(event.getPlayer().hasPermission("hubbly.bypass.antiswear")) return;

            if(Objects.equals(method.toUpperCase(), "CANCEL")) {

                event.setCancelled(true);
                event.getPlayer().sendMessage(Objects.requireNonNull(config.getString("messages.blocked_message")));
                return;

            } else if (Objects.equals(method.toUpperCase(), "STAR")) {

                message = message.replace(word, ChatUtils.repeat("*", word.length()));
                event.setMessage(message);
            }
        }

    }
}
