package me.calrl.hubbly.utils;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.enums.LocaleKey;
import me.calrl.hubbly.managers.LocaleManager;
import net.minecraft.world.entity.ai.behavior.warden.TryToSniff;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MessageBuilder {

    private Player player;
    private Hubbly plugin;
    private LocaleKey key;
    private String content;
    private LocaleManager localeManager = null;
    private boolean usePrefix = true;

    public MessageBuilder() {

    }

    public MessageBuilder(Hubbly plugin) {
        this.plugin = plugin;
        this.localeManager = plugin.getLocaleManager();
    }

    public MessageBuilder(Hubbly plugin, Player player) {
        this.player = player;
        this.plugin = plugin;
        this.localeManager = plugin.getLocaleManager();
    }

    public MessageBuilder setKey(String key) {
        String msg;
        if(player != null) {
            msg = this.localeManager.get(player, key);
        } else {
            msg = this.localeManager.get(localeManager.getDefaultLanguage(), key);
        }
        this.content = (msg != null) ? msg : "[Missing locale: " + key + "]";
        return this;
    }

    public MessageBuilder setMessage(String message) {
        if(message != null) this.content = message;
        return this;
    }

    public MessageBuilder setPlayer(Player player) {
        this.player = player;
        return this;
    }

    public MessageBuilder setPlayer(CommandSender sender) {
        if(sender instanceof Player target) {
            this.setPlayer(target);
        } else {
            setPlayer(null);
        }
        return this;
    }

    public MessageBuilder setPlugin(Hubbly plugin) {
        this.plugin = plugin;
        if(this.localeManager == null) this.localeManager = plugin.getLocaleManager();
        return this;
    }

    /*
    Todo: make a better system
     */

    public MessageBuilder replace(String oldChar, String newChar) {
        if(oldChar == null) {
            oldChar = "oldChar";
        }

        if(newChar == null) {
            newChar = "newChar";

        }

        if(content.contains(oldChar)) {

            this.content = content.replace(oldChar, newChar);
        }
        return this;
    }

    public MessageBuilder usePrefix(boolean bool) {
        this.usePrefix = bool;
        return this;
    }

    public String build() {
        return content;
    }

    public void send() {
        String toSend = ChatUtils.prefixMessage(plugin, player, content);
        if(toSend.contains("nomessage")) {
            return;
        }

        if(player != null) {
            ChatUtils.processMessage(player, toSend);
            player.sendMessage(toSend);
        } else {
            Bukkit.getConsoleSender().sendMessage(toSend);
        }
    }
}
