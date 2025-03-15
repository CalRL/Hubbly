package me.calrl.hubbly.action.actions;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.action.Action;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

public class ClearAction implements Action {
    @Override
    public String getIdentifier() {
        return "CLEAR";
    }

    @Override
    public void execute(Hubbly plugin, Player player, String data) {
        PlayerInventory inventory = player.getInventory();
        inventory.clear();
    }
}
