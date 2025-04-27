package me.calrl.hubbly.managers;

import me.calrl.hubbly.items.PlayerTridentData;
import org.bukkit.entity.Player;
import org.bukkit.entity.Trident;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class TridentDataManager {
    private HashMap<Player, PlayerTridentData> playerTridents;
    public TridentDataManager() {
        this.playerTridents = new HashMap<>();
    }

    public void add(Player player, ItemStack trident, int slot) {
        this.playerTridents.put(player, new PlayerTridentData(trident, slot));
    }

    public void remove(Player player) {
        this.playerTridents.remove(player);
    }

    public void purge() {
        this.playerTridents.clear();
    }

    public void panic() {

    }

}