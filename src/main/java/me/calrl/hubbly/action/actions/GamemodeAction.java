package me.calrl.hubbly.action.actions;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.action.Action;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class GamemodeAction implements Action {
    @Override
    public String getIdentifier() {
        return "GAMEMODE";
    }

    @Override
    public void execute(Hubbly plugin, Player player, String data) {
        player.setGameMode(GameMode.valueOf(data));
    }
}
