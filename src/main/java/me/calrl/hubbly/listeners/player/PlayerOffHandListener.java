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

package me.calrl.hubbly.listeners.player;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.enums.Permissions;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerOffHandListener implements Listener {

    private final Hubbly plugin;
    public PlayerOffHandListener(Hubbly plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    private void onOffHandSwitch(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        if(player.hasPermission(Permissions.BYPASS_OFF_HAND.getPermission())) return;

        else if(plugin.getDisabledWorldsManager().inDisabledWorld(player.getWorld())) return;

        event.setCancelled(true);
    }

    @EventHandler
    private void onInventorySwap(InventoryClickEvent event) {
        HumanEntity player = event.getWhoClicked();
        if(plugin.getDisabledWorldsManager().inDisabledWorld(player.getWorld())) return;
        if(player.hasPermission(Permissions.BYPASS_OFF_HAND.getPermission())) return;

        int rawSlot = event.getRawSlot();
        int slot = event.getSlot();
        ItemStack cursor = event.getCursor();
        if(rawSlot != slot && cursor != null && slot == 40) {
            event.setCancelled(true);
        }
    }

}
