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
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.w3c.dom.Text;

import java.util.Objects;

public class PlayerVisibilityListener implements Listener {

    private final Hubbly plugin;
    private FileConfiguration config = Hubbly.getInstance().getConfig();

    public PlayerVisibilityListener(Hubbly plugin) {
        this.plugin = plugin;
    }
    @EventHandler
    public void onItemClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        if (itemInHand.getType() == Material.LIME_DYE || itemInHand.getType() == Material.GRAY_DYE) {
            if(event.getAction() != Action.PHYSICAL && (player.hasPermission("hubbly.playervisibility.use") || player.isOp())) {
                Bukkit.getScheduler().runTaskLaterAsynchronously(Hubbly.getInstance(), () -> swapDye(player, itemInHand), 1L);
                event.setCancelled(true);

            } else {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("messages.no_permission_use")));
            }
        }
    }
    private void swapDye(Player player, ItemStack itemInHand) {
        Material newMaterial;
        String displayName;
        if (itemInHand.getType() == Material.LIME_DYE) {
            newMaterial = Material.GRAY_DYE;
            displayName = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString("playervisibility.hidden_text")));
            for(Player online : Bukkit.getOnlinePlayers()){
                player.hidePlayer(plugin, online);
            }
        } else {
            newMaterial = Material.LIME_DYE;
            displayName = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString("playervisibility.visible_text")));
            for(Player online : Bukkit.getOnlinePlayers()){
                player.showPlayer(plugin, online);
            }
        }
        ItemStack newItem = new ItemStack(newMaterial);
        ItemMeta meta = newItem.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(displayName);
            newItem.setItemMeta(meta);
        }
        player.getInventory().setItemInMainHand(newItem);
    }
}

