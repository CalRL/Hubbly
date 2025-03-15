package me.calrl.hubbly.action.actions;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.action.Action;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

public class SlotAction implements Action {
    @Override
    public String getIdentifier() {
        return "SLOT";
    }

    @Override
    public void execute(Hubbly plugin, Player player, String data) {
        String[] strings = data.split(";");

        String dataSlot = strings[0];
        if (dataSlot == null ) return;

        int slot = Integer.parseInt(dataSlot);
        PlayerInventory pi = player.getInventory();
        pi.setHeldItemSlot(slot-1);
    }
}
