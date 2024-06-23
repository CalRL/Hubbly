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

import me.calrl.hubbly.functions.CreateCloseItem;
import me.calrl.hubbly.functions.ParsePlaceholders;
import org.bukkit.event.EventHandler;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;


import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

public class CompassListener implements Listener {

    private final Logger logger;
    private final FileConfiguration config;
    private final NamespacedKey itemKey;
    private final JavaPlugin plugin;
    //private final boolean papiInstalled;

    public CompassListener(Logger logger, FileConfiguration config, JavaPlugin plugin) {
        this.logger = logger;
        this.config = config;
        this.plugin = plugin;
        this.itemKey = new NamespacedKey(plugin, "compassItemKey");

        // Register the BungeeCord channel
        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, "BungeeCord");

        // Check if PlaceholderAPI is installed
//        this.papiInstalled = Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");
//        if (papiInstalled) {
//            logger.info("PlaceholderAPI found and enabled.");
//        } else {
//            logger.info("PlaceholderAPI not found. Placeholder support will be disabled.");
//        }
    }

    @EventHandler
    public void onPlayerRightClick(PlayerInteractEvent event) {
        if (event.getAction() != Action.PHYSICAL) {
            Player player = event.getPlayer();
            ItemStack item = player.getInventory().getItemInMainHand();
            if (player.getInventory().getItemInMainHand().getType() == Material.COMPASS && Objects.requireNonNull(item.getItemMeta()).getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString("compass.name"))))) {
                if(player.hasPermission( "hubbly.compass.use") || player.isOp()) {
                    event.setCancelled(true);
                    openCompassGUI(player);
                } else {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString("messages.no_permission_use"))));

                }

//                logger.info("Opened compass GUI for player " + player.getName());
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals(config.getString("compass.gui.title"))) {
            event.setCancelled(true);  // Prevent item movement
            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem != null && clickedItem.hasItemMeta()) {
                ItemMeta meta = clickedItem.getItemMeta();
                assert meta != null;
                PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
                    String itemKeyString = dataContainer.get(itemKey, PersistentDataType.STRING);
                    String command = config.getString("compass.gui.items." + itemKeyString + ".command");
                    if (itemKeyString != null && command != null) {
                        if (command.startsWith("server ")) {
                            sendPlayerToServer(player, command.split(" ")[1]);
                        } else {
                            command = parsePlaceholders(player, command);
                            player.performCommand(command.replace("%player%", player.getName()));
//                                player.sendMessage("Executing command: " + command.replace("%player%", player.getName()));
                        }
                        player.closeInventory();

                }
            }
        }
        ItemStack clickedItem = event.getCurrentItem();
        if(clickedItem == null || clickedItem.getType() == Material.AIR) {
            return;
        }
        if(clickedItem.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString("close_button.name"))))) {
            event.getView().close();
        }
    }

    private void openCompassGUI(Player player) {
        if(Objects.equals(config.getString("compass.enabled"), "true")) {
            Inventory gui = Bukkit.createInventory(null, config.getInt("compass.gui.size"), Objects.requireNonNull(config.getString("compass.gui.title")));
            CreateCloseItem closeItemCreator = new CreateCloseItem(config);
            gui.setItem(config.getInt("close_button.slot")-1, closeItemCreator.createItem());

            for (String itemKey : Objects.requireNonNull(config.getConfigurationSection("compass.gui.items")).getKeys(false)) {
                ItemStack item = createItemFromConfig(player, itemKey);
                if (item != null) {
                    gui.addItem(item);
                }
            }
            if (config.isConfigurationSection("compass.fill")) {
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

    }

    private ItemStack createItemFromConfig(Player player, String itemKey) {
        String path = "compass.gui.items." + itemKey;
        if (!config.contains(path)) {
            return null;
        }

        Material material;
        try {
            material = Material.valueOf(Objects.requireNonNull(config.getString(path + ".material")));
        } catch (IllegalArgumentException e) {
            logger.warning("Invalid material: " + config.getString(path + ".material"));
            return null;
        }

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            if (config.contains(path + ".name")) {
                String itemName = parsePlaceholders(player, config.getString(path + ".name"));
                meta.setDisplayName(itemName);
            }

            if (config.contains(path + ".lore")) {
                List<String> loreList = config.getStringList(path + ".lore");
                for (int i = 0; i < loreList.size(); i++) {
                    loreList.set(i, parsePlaceholders(player, loreList.get(i)));
                }
                meta.setLore(loreList);
            }

            // Add custom key to item's metadata
            PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
            dataContainer.set(this.itemKey, PersistentDataType.STRING, itemKey);

            item.setItemMeta(meta);
        }
        return item;
    }

    private String parsePlaceholders(Player player, String text) {
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            return PlaceholderAPI.setPlaceholders(player, text);
        }
        return text;
    }

    private void sendPlayerToServer(Player player, String server) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        try {
            out.writeUTF("Connect");
            out.writeUTF(server);
            player.sendPluginMessage(plugin, "BungeeCord", b.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
            player.sendMessage("Failed to connect to server: " + server);
        }
    }
    private ItemStack createFillItem(Player player) {
        String materialName = config.getString("compass.fill.type");
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
            String displayName = config.getString("compass.fill.name");
            if (displayName != null) {
                displayName = ParsePlaceholders.parsePlaceholders(player, displayName);
                meta.setDisplayName(displayName);
            }

            item.setItemMeta(meta);
        }

        return item;
    }

}
