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
import me.calrl.hubbly.enums.PluginKeys;
import me.calrl.hubbly.managers.DebugMode;
import me.calrl.hubbly.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.Objects;

public class PlayerVisibilityListener implements Listener {

    private FileConfiguration config = Hubbly.getInstance().getConfig();
    private final Hubbly plugin = Hubbly.getInstance();
    private final DebugMode debugMode = plugin.getDebugMode();

    @EventHandler
    public void onItemClick(PlayerInteractEvent event) {
        if(plugin.getDisabledWorldsManager().inDisabledWorld(event.getPlayer().getWorld())) return;

        Player player = event.getPlayer();


        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        ItemMeta meta = itemInHand.getItemMeta();

        if (meta != null && meta.getPersistentDataContainer().has(PluginKeys.PLAYER_VISIBILITY.getKey())) {
            if(event.getAction() != Action.PHYSICAL && (player.hasPermission("hubbly.use.playervisibility") || player.isOp())) {
                Bukkit.getScheduler().runTaskLater(plugin, () -> swapDye(player, itemInHand), 2L);
                event.setCancelled(true);

            } else {
                player.sendMessage(ChatUtils.translateHexColorCodes(config.getString("messages.no_permission_use")));
            }
            debugMode.info("Holding: " + player.getInventory().getItemInMainHand());
        }
    }
    private void swapDye(Player player, ItemStack itemInHand) {
        Material newMaterial;
        String displayName;
        PersistentDataContainer container = itemInHand.getItemMeta().getPersistentDataContainer();
        String finalString;
        if (Objects.equals(container.get(PluginKeys.PLAYER_VISIBILITY.getKey(), PersistentDataType.STRING), "visible")) {
            newMaterial = Material.valueOf(config.getString("playervisibility.hidden.item", "GRAY_DYE"));
            displayName = ChatUtils.translateHexColorCodes(
                    config.getString("playervisibility.hidden.text"));
            finalString = "hidden";
            for(Player online : Bukkit.getOnlinePlayers()){
                player.hidePlayer(plugin, online);
            }
        } else if(Objects.equals(container.get(PluginKeys.PLAYER_VISIBILITY.getKey(), PersistentDataType.STRING), "hidden")) {
            newMaterial = Material.valueOf(config.getString("playervisibility.visible.item", "LIME_DYE"));
            displayName = ChatUtils.translateHexColorCodes(
                    config.getString("playervisibility.visible.text"));
            finalString = "visible";
            for(Player online : Bukkit.getOnlinePlayers()){
                player.showPlayer(plugin, online);
            }
        } else {
            plugin.getLogger().info("Error! Please report to developer!");
            return;
        }

        ItemStack newItem = new ItemStack(newMaterial);
        ItemMeta meta = newItem.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(displayName);
            meta.getPersistentDataContainer().set(PluginKeys.PLAYER_VISIBILITY.getKey(), PersistentDataType.STRING, finalString);
            newItem.setItemMeta(meta);
        }
        player.getInventory().setItemInMainHand(newItem);
    }
}

