package me.calrl.hubbly.action.actions;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.action.Action;
import me.calrl.hubbly.utils.ChatUtils;
import me.calrl.hubbly.utils.Utils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class LinkAction implements Action {
    @Override
    public String getIdentifier() {
        return "LINK";
    }

    @Override
    public void execute(Hubbly plugin, Player player, String data) {
        String[] texts = data.split(";", 3);
        Logger logger = plugin.getLogger();
        if(texts.length != 3) {
            logger.warning("Error: LINK action does not have 3 arguments");
            return;
        }

        String message = texts[0].trim();
        String hoverText = texts[1].trim();
        String link = texts[2].trim();

        TextComponent component = ChatUtils.textLinkBuilder(message, Utils.normalizeUrl(link), hoverText, player);
        player.spigot().sendMessage(component);
    }
}
