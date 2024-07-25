package me.calrl.hubbly.action.actions;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.action.Action;
import me.calrl.hubbly.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class BroadcastAction implements Action {
    @Override
    public String getIdentifier() {
        return "BROADCAST";
    }

    @Override
    public void execute(Hubbly plugin, Player player, String data) {
        Bukkit.broadcastMessage(ChatUtils.translateHexColorCodes(data));
    }
}
