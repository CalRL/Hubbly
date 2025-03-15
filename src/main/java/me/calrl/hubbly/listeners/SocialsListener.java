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
import me.calrl.hubbly.enums.PluginKeys;
import me.calrl.hubbly.functions.CreateCloseItem;
import me.calrl.hubbly.functions.CreateCustomHead;
import me.calrl.hubbly.functions.ParsePlaceholders;
import me.calrl.hubbly.managers.holders.SocialsHolder;
import me.calrl.hubbly.utils.ChatUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

public class SocialsListener implements Listener {

    private FileConfiguration config = Hubbly.getInstance().getConfig();
    private Hubbly plugin;
    private Logger logger;
    public SocialsListener(Hubbly plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
    }

    @EventHandler
    private void onPlayerInteract(PlayerInteractEvent event) {
        if(Hubbly.getInstance().getDisabledWorldsManager().inDisabledWorld(event.getPlayer().getLocation())) return;
        ItemStack item = event.getItem();

        if (item == null) {
            return;
        }
        ItemMeta meta = item.getItemMeta();
        if(meta == null) return;
        if(event.getAction() != Action.PHYSICAL && meta.getPersistentDataContainer().has(PluginKeys.SOCIALS.getKey())) {

            if(event.getPlayer().hasPermission("hubbly.use.socials") || event.getPlayer().isOp()) {
                openSocialsGUI(event.getPlayer());
            } else {
                event.getPlayer().sendMessage(ChatUtils.translateHexColorCodes(config.getString("messages.no_permission_use")));
            }

        }

    }

    @EventHandler
    private void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        ItemStack heldItem = player.getInventory().getItemInMainHand();

        Material mat = heldItem.getType();
        if(mat != Material.PLAYER_HEAD) return;

        ItemMeta meta = heldItem.getItemMeta();
        if(meta == null) return;

        if(meta.getPersistentDataContainer().has(PluginKeys.SOCIALS.getKey())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack heldItem = player.getInventory().getItemInMainHand();

        Material mat = heldItem.getType();
        if(mat != Material.PLAYER_HEAD) return;

        ItemMeta meta = heldItem.getItemMeta();
        if(meta == null) return;

        if(meta.getPersistentDataContainer().has(PluginKeys.SOCIALS.getKey())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onInventoryClick(InventoryClickEvent event) {
        ItemStack clickedItem = event.getCurrentItem();
        if(clickedItem == null || clickedItem.getType() == Material.AIR) {
            return;
        }

        InventoryHolder holder = event.getInventory().getHolder();
        if(!(holder instanceof SocialsHolder)) return;


        event.setCancelled(true);

        ItemMeta meta = clickedItem.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) {
            return;
        }

        if(clickedItem.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString("close_button.name"))))) {
            event.getView().close();
        }

        Player player = (Player) event.getWhoClicked();
        String clickedDisplayName = meta.getDisplayName();

        for (String itemKey : Objects.requireNonNull(config.getConfigurationSection("socials.items")).getKeys(false)) {
            String itemDisplayName = ChatUtils.processMessage(player, config.getString("socials.items." + itemKey + ".name"));
            if (itemDisplayName != null && itemDisplayName.equals(clickedDisplayName)) {
                String message = config.getString("socials.items." + itemKey + ".message");
                if (message != null) {
                    player.closeInventory();
                    message = ChatUtils.processMessage(player, message);
                    player.spigot().sendMessage(ChatUtils.textLinkBuilder(message, config.getString( "socials.items." + itemKey + ".link"), config.getString("socials.items." + itemKey + ".hover")));

                }
                break;
            }
        }
    }

    private void openSocialsGUI(Player player) {
        Inventory gui = new SocialsHolder(plugin).getInventory();

        CreateCloseItem closeItemCreator = new CreateCloseItem(config);
        gui.setItem(config.getInt("close_button.slot")-1, closeItemCreator.createItem(config, "close_button"));

        for (String itemKey : Objects.requireNonNull(config.getConfigurationSection("socials.items")).getKeys(false)) {
            int slot = config.getInt("socials.items." + itemKey + ".slot", -1) - 1; // Adjusted the slot calculation
            if (slot >= 0 && slot < gui.getSize()) {
                ItemStack item = createItemFromConfig(player, itemKey);
                if (item != null) {
                    gui.setItem(slot, item);
                }
            }
        }

        if (config.isConfigurationSection("socials.fill")) {
            ItemStack fillItem = createFillItem(player);
            if (fillItem != null) {
                for (int i = 0; i < gui.getSize(); i++) {
                    if (gui.getItem(i) == null || gui.getItem(i).getType() == Material.AIR) {
                        gui.setItem(i, fillItem);
                    }
                }
            }
        }

        player.openInventory(gui);
    }

    private ItemStack createItemFromConfig(Player player, String itemKey) {
        String materialName = config.getString("socials.items." + itemKey + ".material");
        if (materialName == null) {
            logger.warning("Material not set for item " + itemKey + " in configuration.");
            return null;
        }

        Material material = Material.getMaterial(materialName);
        if (material == null) {
            logger.warning("Invalid material " + materialName + " for item " + itemKey + " in configuration.");
            return null;
        }

        ItemStack item;
        if (material == Material.PLAYER_HEAD) {
            String textureValue = config.getString("socials.items." + itemKey + ".value");
            String displayName = config.getString("socials.items." + itemKey + ".name");
            if (textureValue == null || displayName == null) {
                //logger.warning("Texture value or display name not set for PLAYER_HEAD item " + itemKey + " in configuration.");
                return null;
            }
            displayName = ChatUtils.processMessage(player, displayName);
            //logger.info("Creating custom head with texture value: " + textureValue + " and display name: " + displayName);
            item = CreateCustomHead.createCustomHead(textureValue, displayName);
        } else {
            item = new ItemStack(material);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                String displayName = ChatUtils.processMessage(player, config.getString("socials.items." + itemKey + ".name", "Socials"));
                displayName = ChatUtils.processMessage(player, displayName);
                meta.setDisplayName(displayName);

                List<String> lore = config.getStringList("socials.items." + itemKey + ".lore");
                if (lore != null && !lore.isEmpty()) {
                    for (int i = 0; i < lore.size(); i++) {
                        lore.set(i, ChatUtils.processMessage(player, lore.get(i)));
                    }
                    meta.setLore(lore);
                }

                item.setItemMeta(meta);
            }
        }

        return item;
    }

    private ItemStack createFillItem(Player player) {
        String materialName = config.getString("socials.fill.type");
        if (materialName == null) {
            logger.warning("Material not set for fill item in configuration.");
            return null;
        }

        Material material = Material.getMaterial(materialName);
        if (material == null) {
            logger.warning("Invalid material " + materialName + " for fill item in configuration.");
            return null;
        }

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            String displayName = config.getString("socials.fill.name");
            if (displayName != null) {
                displayName = ParsePlaceholders.parsePlaceholders(player, displayName);
                meta.setDisplayName(displayName);
            }

            item.setItemMeta(meta);
        }

        return item;
    }

}
