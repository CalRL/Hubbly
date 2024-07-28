package me.calrl.hubbly.listeners.chat;

import me.calrl.hubbly.Hubbly;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.List;
import java.util.Objects;

import static jdk.internal.joptsimple.internal.Strings.repeat;

public class ChatListener implements Listener {
    private final Hubbly plugin;
    private List<String> blockedWords;
    public ChatListener(Hubbly plugin) {
        this.plugin = plugin;
        blockedWords = plugin.getConfig().getStringList("blocked_words.words");
    }

    @EventHandler
    private void onPlayerChat(AsyncPlayerChatEvent event) {
        FileConfiguration config = plugin.getConfig();
        if(plugin.getDisabledWorldsManager().inDisabledWorld(event.getPlayer().getWorld())) return;
        if(!config.getBoolean("blocked_words.enabled")) return;

        String message = event.getMessage();
        String method = config.getString("blocked_words.method");
        
        for(String word : blockedWords) {
            if(Objects.equals(method.toUpperCase(), "CANCEL")) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(Objects.requireNonNull(config.getString("messages.blocked_message")));
                return;
            } else if (Objects.equals(method.toUpperCase(), "STAR")) {
                message = message.replace(word, repeat('*', word.length()));
                event.setMessage(message);
            }
        }

    }
}
