package me.calrl.hubbly.action.actions;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.action.Action;
import org.bukkit.entity.Player;

public class CloseAction implements Action {
    @Override
    public String getIdentifier() {
        return "CLOSE";
    }

    @Override
    public void execute(Hubbly plugin, Player player, String data) {
        player.closeInventory();
    }
}
