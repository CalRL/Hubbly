package me.calrl.hubbly.action.actions;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.action.Action;
import me.calrl.hubbly.utils.ChatUtils;
import org.bukkit.entity.Player;

public class MessageAction implements Action {
    @Override
    public String getIdentifier() {
        return "MESSAGE";
    }

    @Override
    public void execute(Hubbly plugin, Player player, String data) {

        player.sendMessage(ChatUtils.translateHexColorCodes(data));
    }
}
