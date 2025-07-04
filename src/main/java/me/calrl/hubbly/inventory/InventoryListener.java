package me.calrl.hubbly.inventory;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.action.Action;
import me.calrl.hubbly.action.ActionManager;
import me.calrl.hubbly.enums.PluginKeys;
import me.calrl.hubbly.managers.DebugMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

//todo: delete this
public class InventoryListener implements Listener {

    private final Hubbly plugin;
    private final NamespacedKey actionsKey;
    private final DebugMode debugMode;
    public InventoryListener(Hubbly plugin) {
        this.plugin = plugin;
        this.actionsKey = PluginKeys.ACTIONS_KEY.getKey();
        this.debugMode = plugin.getDebugMode();
    }

    @EventHandler
    private void onInventoryClick(InventoryClickEvent event) {

        if(!(event.getView().getTopInventory().getHolder() instanceof InventoryBuilder)) {
            return;
        }

        event.setCancelled(true);

        if(!(event.getWhoClicked() instanceof Player player)) return;

        ItemStack clickedItem = event.getCurrentItem();
        if(clickedItem == null || clickedItem.getType() == Material.AIR) return;

        ItemMeta meta = clickedItem.getItemMeta();
        if(meta == null) return;

        PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
        String actionsString = dataContainer.get(actionsKey, PersistentDataType.STRING);
        if (actionsString == null) {
            return;
        }
        plugin.getDebugMode().info("Actions string found: " + actionsString);
        ActionManager actionManager = plugin.getActionManager();
        actionManager.executeActions(player, actionsString);

    }
}
