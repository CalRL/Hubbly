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

import java.util.Objects;
import java.util.logging.Logger;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.items.CompassItem;
import me.calrl.hubbly.items.PlayerVisibilityItem;
import me.calrl.hubbly.items.ShopItem;
import me.calrl.hubbly.items.SocialsItem;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;

public class ItemJoinListener implements Listener {
    private final Logger logger;
    private FileConfiguration config = Hubbly.getInstance().getConfig();
    private final JavaPlugin plugin;


    public ItemJoinListener(Logger logger, JavaPlugin plugin) {
        this.logger = logger;
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        PlayerInventory playerInventory = event.getPlayer().getInventory();
        playerInventory.clear();
        playerInventory.setHeldItemSlot(4);
        if(Objects.equals(config.getBoolean("item_on_join.enabled"), true)) {
            if(Objects.equals(config.getBoolean("item_on_join.features.compass.enabled"), true)) {
                event.getPlayer().getInventory().setItem(config.getInt("item_on_join.features.compass.slot")-1, new CompassItem().createItem());
            }
            if(Objects.equals(config.getBoolean("item_on_join.features.shop.enabled"), true)) {
                event.getPlayer().getInventory().setItem(config.getInt("item_on_join.features.shop.slot")-1, new ShopItem().createItem());
            }
            if(Objects.equals(config.getBoolean("item_on_join.features.socials.enabled"), true)) {
                event.getPlayer().getInventory().setItem(config.getInt("item_on_join.features.socials.slot")-1, new SocialsItem().createItem());
            }
            if(Objects.equals(config.getBoolean("item_on_join.features.playervisibility.enabled"), true)) {
                event.getPlayer().getInventory().setItem(config.getInt("item_on_join.features.playervisibility.slot")-1, new PlayerVisibilityItem().createItem());
            }

        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        boolean forceInventory = plugin.getConfig().getBoolean("item_on_join.forceinventory");
        Player player = (Player) event.getWhoClicked();

        if (forceInventory && !player.hasPermission("hubbly.items.bypass")) {
            event.setCancelled(true);
            player.setItemOnCursor(new ItemStack(Material.AIR));
            player.updateInventory();
        }
    }

}
