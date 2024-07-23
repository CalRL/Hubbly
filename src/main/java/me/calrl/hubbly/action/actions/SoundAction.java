package me.calrl.hubbly.action.actions;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.action.Action;
import me.calrl.hubbly.managers.DebugMode;
import org.bukkit.entity.Player;

public class SoundAction implements Action {
    @Override
    public String getIdentifier() {
        return "SOUND";
    }

    @Override
    public void execute(Hubbly plugin, Player player, String data) {
        DebugMode debugMode = plugin.getDebugMode();
        try {
            player.playSound(player.getLocation(), data, 1L, 1L);
        } catch (Exception e) {
            debugMode.severe("Sound action failed: " + e);
        }

    }
}
