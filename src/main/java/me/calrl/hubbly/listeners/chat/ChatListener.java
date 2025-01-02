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
import me.calrl.hubbly.enums.Permissions;
import me.calrl.hubbly.managers.DebugMode;
import me.calrl.hubbly.utils.ChatUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


public class ChatListener implements Listener {
    private final Hubbly plugin;
    private boolean isChatLocked;
    private final List<String> blockedWords;
    private final DebugMode debugMode;
    public ChatListener(Hubbly plugin) {
        this.plugin = plugin;
        blockedWords = plugin.getConfig().getStringList("blocked_words.words").stream().map(String::toLowerCase).collect(Collectors.toList());
        debugMode = plugin.getDebugMode();
    }

    @EventHandler
    private void checkChatLock(AsyncPlayerChatEvent event) {
        if(plugin.getLockChat().getChatLock() && !event.getPlayer().hasPermission(Permissions.BYPASS_CHAT_LOCK.getPermission())) {
            event.setCancelled(true);
        }
    }
    @EventHandler
    private void onPlayerChat(AsyncPlayerChatEvent event) {
        FileConfiguration config = plugin.getConfig();
        if(config.getBoolean("blocked_words.enabled") && !plugin.getDisabledWorldsManager().inDisabledWorld(event.getPlayer().getWorld())) {

            String playerName = event.getPlayer().getName();
            String message = event.getMessage().toLowerCase();
            String method = config.getString("blocked_words.method");

            String regex = String.join("|", blockedWords.stream().map(Pattern::quote).toList());
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(message);

            if (matcher.find()) {
                String blockedWord = matcher.group();

                if (Objects.equals(method.toUpperCase(), "CANCEL")) {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage(
                            Objects.requireNonNull(config.getString("messages.blocked_message"))
                    );
                    debugMode.info(playerName + " sent a blocked word: " + blockedWord);
                    return;
                } else if (Objects.equals(method.toUpperCase(), "STAR")) {
                    message = matcher.replaceAll(match -> ChatUtils.repeat("*", match.group().length()));
                    event.setMessage(message);
                    debugMode.info(playerName + " sent a blocked word: " + blockedWord);
                }

            }
        }
    }
}
