/*
 * This file is part of Hubbly.
 *
 * Hubbly is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Hubbly is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Hubbly. If not, see <http://www.gnu.org/licenses/>.
 */

package me.calrl.hubbly.listeners;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.action.ActionManager;
import me.calrl.hubbly.enums.Permissions;
import me.calrl.hubbly.enums.PluginKeys;
import me.calrl.hubbly.inventory.CompassInventory;
import me.calrl.hubbly.managers.DebugMode;
import me.calrl.hubbly.managers.holders.CompassHolder;
import me.calrl.hubbly.utils.ChatUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class CompassListener implements Listener {

    private FileConfiguration config;
    private final Hubbly plugin;
    private final ActionManager actionManager;
    private final NamespacedKey actionsKey;
    private DebugMode debugMode;
    private FileConfiguration pluginConfig;


    public CompassListener(Hubbly plugin) {
        this.plugin = plugin;
        this.config = plugin.getServerSelectorConfig();
        this.actionManager = plugin.getActionManager();
        this.actionsKey = new NamespacedKey(plugin, "customActions");
        this.debugMode = plugin.getDebugMode();
        pluginConfig = plugin.getConfig();
    }

    @EventHandler
    public void onPlayerRightClick(PlayerInteractEvent event) {
        if(plugin.getDisabledWorldsManager().inDisabledWorld(event.getPlayer().getWorld())) return;

        Action action = event.getAction();
        if (action == Action.PHYSICAL) return;

        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item == null || !item.hasItemMeta()) {
            return;
        }

        if (item.getItemMeta() != null &&  item.getItemMeta().getPersistentDataContainer().has(PluginKeys.SELECTOR.getKey())) {
            if (player.hasPermission(Permissions.USE_SELECTOR.getPermission())) {
                event.setCancelled(true);
                openCompassGUI(player);
            } else {
                player.sendMessage(ChatUtils.translateHexColorCodes(
                        pluginConfig.getString("messages.no_permission_use"))
                );
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (config == null) {
            debugMode.warn("Configuration is missing.");
            return;
        }
        InventoryHolder holder = event.getInventory().getHolder();
        if(!(holder instanceof CompassHolder)) return;
        event.setCancelled(true);

        ItemStack clickedItem = event.getCurrentItem();
        if(clickedItem == null || clickedItem.getType() == Material.AIR) return;

        ItemMeta meta = clickedItem.getItemMeta();
        if(meta == null) return;

        PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
        String actionsString = dataContainer.get(actionsKey, PersistentDataType.STRING);
        if (actionsString == null) {
            return;
        }
        debugMode.info("Actions string found: " + actionsString);
        Player player = (Player) event.getWhoClicked();

        actionManager.executeActions(player, actionsString);
    }

    public void openCompassGUI(Player player) {
        boolean isEnabled = config.getBoolean("selector.enabled", true);
        if(!isEnabled) {
            return;
        }

        CompassInventory compassInventory = new CompassInventory(plugin, player);
        Inventory gui = compassInventory.getInventory();
        player.openInventory(gui);
    }

}
