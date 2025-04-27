package me.calrl.hubbly.items;

import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class PlayerTridentData {
    private int slot;
    private ItemStack trident;
    private UUID playerId;
    public PlayerTridentData(ItemStack trident, int slot) {
        this.slot = slot;
        this.trident = trident;
    }

    public ItemStack getTrident() {
        return this.trident;
    }

    public int getSlot() {
        return this.slot;
    }
}
