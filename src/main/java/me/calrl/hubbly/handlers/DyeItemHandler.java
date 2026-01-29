package me.calrl.hubbly.handlers;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.items.DyeItem;
import me.calrl.hubbly.managers.PlayerVisibilityManager;
import me.calrl.hubbly.managers.StorageManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;

public class DyeItemHandler {
    private final Hubbly plugin;
    private final Player player;
    public DyeItemHandler(Hubbly plugin, Player player) {
        this.player = player;
        this.plugin = plugin;
    }

    /**
     * Handle item update on join based on nbt / storage data
     */
    public void handleJoin() {
        // get the item
        // get state in db
        // change item to what appears in db
        StorageManager storage = this.plugin.getStorageManager();
        if(storage.isActive() && plugin.getConfig().getBoolean("database.enabled")) {

            return;
        }
    }

    /**
     * Check to see whether or not the player has the visibility item
     */
    public boolean hasItem() {
        for(ItemStack item : player.getInventory()) {
//            if(item.getType() != Material.GRAY_DYE)
//            ItemMeta meta = item.getItemMeta();
//            if(meta == null) continue;
//
//            PersistentDataContainer container =
        }
        return true;
    }
}
