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
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.event.Listener;

import java.util.Objects;
import java.util.logging.Logger;

public class ShopListener implements Listener {
    private final Logger logger;
    private FileConfiguration config = Hubbly.getInstance().getConfig();
    private Hubbly plugin = Hubbly.getInstance();

    public ShopListener() {
        this.logger = plugin.getLogger();
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if(Hubbly.getInstance().getDisabledWorldsManager().inDisabledWorld(event.getPlayer().getLocation())) return;
        Player player = event.getPlayer();
        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        if (itemInHand.getType() == Material.valueOf(config.getString("shop.item.type")) && (player.hasPermission("hubbly.shop.use") || player.isOp())) {
            player.performCommand(Objects.requireNonNull(config.getString("shop.command")));
        }
    }
}
