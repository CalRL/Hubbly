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
import me.calrl.hubbly.items.ConfigItems;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class ConfigItemListener implements Listener {

    private final Logger logger;
    private final NamespacedKey customCommandsKey;

    public ConfigItemListener(JavaPlugin plugin) {
        this.logger = plugin.getLogger();
        this.customCommandsKey = new NamespacedKey(plugin, "customCommands");
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item == null || item.getType() == Material.AIR) {
            return;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return;
        }

        String nbtData = meta.getPersistentDataContainer().get(customCommandsKey, PersistentDataType.STRING);

        if (event.getAction() != Action.PHYSICAL) {
            if (meta.getPersistentDataContainer().has(customCommandsKey, PersistentDataType.STRING)) {
                // Display NBT data in chat for testing purposes
                player.sendMessage("NBT Data: " + nbtData);

                // Execute commands
                ConfigItems configItems = new ConfigItems("");
                configItems.executeCommands(player, item);
                event.setCancelled(true);
            }
        }
    }
}
