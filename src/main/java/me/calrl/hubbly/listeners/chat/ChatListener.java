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


public class ChatListener implements Listener {
    private final Hubbly plugin;
    private final List<String> blockedWords;
    private final DebugMode debugMode;
    public ChatListener(Hubbly plugin) {
        this.plugin = plugin;
        blockedWords = plugin.getConfig().getStringList("blocked_words.words");
        debugMode = plugin.getDebugMode();
    }

    @EventHandler
    private void onPlayerChat(AsyncPlayerChatEvent event) {
        FileConfiguration config = plugin.getConfig();
        if(!config.getBoolean("blocked_words.enabled")) return;
        if(plugin.getDisabledWorldsManager().inDisabledWorld(event.getPlayer().getWorld())) return;


        String message = event.getMessage();
        String method = config.getString("blocked_words.method");

        for(String word : blockedWords) {
            if(method == null) {
                debugMode.info("blocked_words.method is null in config");
                return;
            }

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
