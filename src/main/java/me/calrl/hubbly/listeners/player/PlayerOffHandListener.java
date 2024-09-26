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
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

public class PlayerOffHandListener implements Listener {

    @EventHandler
    private void onOffHandSwitch(PlayerSwapHandItemsEvent event) {
        if(event.getPlayer().hasPermission("hubbly.bypass") || event.getPlayer().isOp()) return;
        else if(Hubbly.getInstance().getDisabledWorldsManager().inDisabledWorld(event.getPlayer().getWorld())) return;
        event.setCancelled(true);
    }

    @EventHandler
    private void onInventorySwap(InventoryClickEvent event) {
        if(event.getWhoClicked().hasPermission("hubbly.bypass") || event.getWhoClicked().isOp()) return;
        if(event.getRawSlot() != event.getSlot() && event.getCursor() != null && event.getSlot() == 40) {
            event.setCancelled(true);
        }
    }

}
