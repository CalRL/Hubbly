package me.calrl.hubbly.managers;

import me.calrl.hubbly.items.PlayerTridentData;
import me.calrl.hubbly.service.ILifecycle;
import org.bukkit.entity.Player;
import org.bukkit.entity.Trident;
import org.bukkit.inventory.ItemStack;
import org.yaml.snakeyaml.constructor.DuplicateKeyException;

import java.util.HashMap;

public class TridentDataManager implements ILifecycle {
    private HashMap<Player, PlayerTridentData> playerTridents;

    public void add(Player player, ItemStack trident, int slot) {
        if(playerTridents.containsKey(player)) {
            new DebugMode().info("Player already has a tracked trident");
            return;
        }
        this.playerTridents.put(player, new PlayerTridentData(trident, slot));
    }

    public void remove(Player player) {
        if(playerTridents.containsKey(player)) {
            this.playerTridents.remove(player);
        }
    }

    public PlayerTridentData get(Player player) {
        if(playerTridents.containsKey(player)) {
            return playerTridents.get(player);
        }

        throw new NullPointerException("No valid trident found. Player does not have a tracked trident...");
    }

    public void purge() {
        this.playerTridents.clear();
    }

    @Override
    public void onEnable() {
        this.playerTridents = new HashMap<>();
    }

    @Override
    public void onReload() {
        this.purge();
    }

    @Override
    public void onDisable() {
        this.purge();
    }
}