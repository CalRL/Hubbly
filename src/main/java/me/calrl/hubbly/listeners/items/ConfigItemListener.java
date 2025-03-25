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

package me.calrl.hubbly.listeners.items;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.enums.Permissions;
import me.calrl.hubbly.enums.PluginKeys;
import me.calrl.hubbly.items.ConfigItem;
import me.calrl.hubbly.action.ActionManager;
import me.calrl.hubbly.managers.DebugMode;
import me.calrl.hubbly.managers.DisabledWorlds;
import me.calrl.hubbly.managers.ItemsManager;
import me.calrl.hubbly.managers.cooldown.CooldownManager;
import me.calrl.hubbly.managers.cooldown.CooldownType;
import me.calrl.hubbly.utils.MessageBuilder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.TileState;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class ConfigItemListener implements Listener {

    private final Logger logger;
    private final NamespacedKey customActionsKey;
    private final ActionManager actionManager;
    private final DebugMode debugMode;
    private final Hubbly plugin;
    private final ItemsManager itemsManager;

    public ConfigItemListener(Hubbly plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.customActionsKey = new NamespacedKey(plugin, "customActions");
        this.actionManager = plugin.getActionManager();
        this.debugMode = plugin.getDebugMode();
        this.itemsManager = plugin.getItemsManager();
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if(plugin.getDisabledWorldsManager().inDisabledWorld(event.getPlayer().getWorld())) return;
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item == null || item.getType() == Material.AIR) {
            return;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return;
        }

        if(player.isSneaking()) {
            return;
        }

        if (event.getAction() != Action.PHYSICAL && event.getHand() == EquipmentSlot.HAND) {
            if (meta.getPersistentDataContainer().has(customActionsKey, PersistentDataType.STRING)) {
                // Execute actions
                itemsManager.executeActions(player, item);
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        DisabledWorlds disabledWorlds = plugin.getDisabledWorldsManager();
        Player player = event.getPlayer();
        Location location = player.getLocation();
        if(disabledWorlds.inDisabledWorld(location)) return;

        Block blockStandingOn = location.getBlock();
        Block blockBelow = location.subtract(0, 1, 0).getBlock();

        TileState blockStandingOnData = (TileState) blockStandingOn.getState();
        TileState blockBelowData = (TileState) blockBelow.getState();

        PersistentDataContainer blockStandingOnContainer = blockStandingOnData.getPersistentDataContainer();
        PersistentDataContainer blockBelowContainer = blockBelowData.getPersistentDataContainer();
        boolean done = false;
        NamespacedKey key = PluginKeys.ACTIONS_KEY.getKey();
        if(blockBelowContainer.has(key)) {
            done = true;
            String actionData = blockBelowContainer.get(key, PersistentDataType.STRING);
            actionManager.executeActions(player, actionData);
        }

        if(blockStandingOnContainer.has(key) && !done) {
            String actionData = blockStandingOnContainer.get(key, PersistentDataType.STRING);
            actionManager.executeActions(player, actionData);
        }
    }

}
