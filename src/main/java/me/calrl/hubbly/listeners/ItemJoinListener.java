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
import me.calrl.hubbly.items.SocialsItem;
import me.calrl.hubbly.managers.DebugMode;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Debug;

public class ItemJoinListener implements Listener {
    private final Logger logger;
    private FileConfiguration config = Hubbly.getInstance().getConfig();
    private final Hubbly plugin;
    private final DebugMode debugMode;


    public ItemJoinListener(Logger logger, Hubbly plugin) {
        this.logger = logger;
        this.plugin = plugin;
        this.debugMode = plugin.getDebugMode();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if(plugin.getDisabledWorldsManager().inDisabledWorld(event.getPlayer().getLocation())) return;
        PlayerInventory playerInventory = event.getPlayer().getInventory();
        playerInventory.clear();
        playerInventory.setHeldItemSlot(config.getInt("item_on_join.features.compass.slot")-1);

        /*
         * Garbage, this is doodoo but I don't want to remove it in favor of [ITEM] because some people still use this, no clue why
         * TODO: fix this
         */
        if(Objects.equals(config.getBoolean("item_on_join.enabled"), true)) {
            if(Objects.equals(config.getBoolean("item_on_join.features.compass.enabled"), true)) {
                event.getPlayer().getInventory().setItem(config.getInt("item_on_join.features.compass.slot")-1, new CompassItem().createItem());
            }
            if(Objects.equals(config.getBoolean("item_on_join.features.socials.enabled"), true)) {
                event.getPlayer().getInventory().setItem(config.getInt("item_on_join.features.socials.slot")-1, new SocialsItem().createItem());
            }
            if(Objects.equals(config.getBoolean("item_on_join.features.playervisibility.enabled"), true)) {
                event.getPlayer().getInventory().setItem(config.getInt("item_on_join.features.playervisibility.slot")-1, new PlayerVisibilityItem().createItem());
            }
            debugMode.info("ItemOnJoin finished...");
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        boolean forceInventory = plugin.getConfig().getBoolean("item_on_join.forceinventory");
        Player player = (Player) event.getWhoClicked();

        if (forceInventory && !player.hasPermission("hubbly.bypass.forceinventory")) {
            event.setCancelled(true);
            player.setItemOnCursor(new ItemStack(Material.AIR));
            player.updateInventory();
        }
    }

}
