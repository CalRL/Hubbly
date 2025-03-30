package me.calrl.hubbly.listeners;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.action.ActionManager;
import me.calrl.hubbly.enums.PluginKeys;
import me.calrl.hubbly.inventory.InventoryBuilder;
import me.calrl.hubbly.managers.DebugMode;
import me.calrl.hubbly.utils.xseries.XMaterial;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class MenuListener implements Listener {

    private Hubbly plugin;
    private final ActionManager actionManager;
    private final NamespacedKey actionsKey;
    private final DebugMode debugMode;

    public MenuListener(Hubbly plugin) {
        this.plugin = plugin;
        this.actionManager = plugin.getActionManager();
        this.actionsKey = PluginKeys.ACTIONS_KEY.getKey();
        this.debugMode = new DebugMode(plugin);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();
        if(!(holder instanceof InventoryBuilder)) {
            return;
        }

        ItemStack eventItem = event.getCurrentItem();
        if(eventItem == null
                || eventItem.getType() == XMaterial
                .matchXMaterial(Material.AIR)
                .parseMaterial())  {
            return;
        }

        ItemMeta itemMeta = eventItem.getItemMeta();
        if(itemMeta == null) return;

        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        String actionString = container.get(this.actionsKey, PersistentDataType.STRING);

        if(actionString == null) return;

        debugMode.info("Action data found: " + actionString);

        if(event.getWhoClicked() instanceof Player player) {
            actionManager.executeActions(player, actionString);
        }

    }

}
