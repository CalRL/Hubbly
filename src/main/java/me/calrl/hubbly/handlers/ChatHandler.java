package me.calrl.hubbly.handlers;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.enums.Permissions;
import me.calrl.hubbly.service.ILifecycle;
import me.calrl.hubbly.managers.DebugMode;
import me.calrl.hubbly.managers.DisabledWorlds;
import me.calrl.hubbly.utils.ChatUtils;
import me.calrl.hubbly.utils.MessageBuilder;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatHandler implements ILifecycle {
    private final Hubbly plugin;
    private final DebugMode debugMode;
    private Pattern pattern;
    public ChatHandler(Hubbly plugin) {
        this.plugin = plugin;
        this.debugMode = new DebugMode(plugin);
        this.onEnable();
    }

    public void handle(AsyncPlayerChatEvent event) {
        final Player player = event.getPlayer();
        final String permission = Permissions.BYPASS_CHAT_LOCK.getPermission();
        final boolean hasPermission = player.hasPermission(permission);
        if(plugin.services().lockChat().isLocked() && !hasPermission) {
            event.setCancelled(true);
            return;
        }


        final DisabledWorlds disabledWorlds = plugin.services().disabledWorlds();
        final World playerWorld = player.getWorld();
        final boolean notInDisabledWorld = !disabledWorlds.inDisabledWorld(playerWorld);
        FileConfiguration config = plugin.getConfig();
        if(config.getBoolean("blocked_words.enabled") && notInDisabledWorld) {
            final List<String> blockedWords = config.getStringList("blocked_words.words").stream().map(String::toLowerCase).toList();

            if (blockedWords.isEmpty()) {
                return;
            }

            String message = event.getMessage().toLowerCase();
            final String playerName = event.getPlayer().getName();
            final String method = config.getString("blocked_words.method", "CANCEL");

            final String regex = String.join("|", blockedWords.stream().map(Pattern::quote).toList());
            final Pattern pattern = Pattern.compile(regex);
            final Matcher matcher = pattern.matcher(message);

            if(!matcher.find()) {
                return;
            }

            final String blockedWord = matcher.group();
            debugMode.info(playerName + " message: " + blockedWord);
            if (Objects.equals(method.toUpperCase(), "CANCEL")) {
                event.setCancelled(true);
                new MessageBuilder(plugin, event.getPlayer()).setKey("blocked_message").send();
            } else if (Objects.equals(method.toUpperCase(), "STAR")) {
                message = matcher.replaceAll(match -> ChatUtils.repeat("*", match.group().length()));
                event.setMessage(message);
            }

            debugMode.info(playerName + " sent a blocked word: " + blockedWord);
        }
    }

    @Override
    public void onEnable() {
        FileConfiguration config = this.plugin.getConfig();
        final List<String> blockedWords = config.getStringList("blocked_words.words").stream().map(String::toLowerCase).toList();
        final String regex = String.join("|", blockedWords.stream().map(Pattern::quote).toList());
        this.pattern = Pattern.compile(regex);

    }

    @Override
    public void onReload() {
        FileConfiguration config = this.plugin.getConfig();
        final List<String> blockedWords = config.getStringList("blocked_words.words").stream().map(String::toLowerCase).toList();
        final String regex = String.join("|", blockedWords.stream().map(Pattern::quote).toList());
        this.pattern = Pattern.compile(regex);
    }

    @Override
    public void onDisable() {

    }
}
