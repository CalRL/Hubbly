package me.calrl.hubbly.action.actions;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.action.Action;
import me.calrl.hubbly.utils.ChatUtils;
import net.md_5.bungee.api.chat.ClickEvent;
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
        List<String> texts = List.of(data.split(";"));
        Logger logger = plugin.getLogger();
        if(texts.size() != 3) {
            logger.warning("Error: LINK action does not have 3 arguments");
            return;
        }

        String message = texts.get(0);
        String hoverText = texts.get(1);
        String link = texts.get(2);

        TextComponent component = ChatUtils.textLinkBuilder(message, link, hoverText, player);
        player.spigot().sendMessage(component);
    }
}
