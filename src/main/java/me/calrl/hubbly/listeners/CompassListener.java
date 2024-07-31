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
import me.calrl.hubbly.functions.CreateCustomHead;
import me.calrl.hubbly.action.ActionManager;
import me.calrl.hubbly.managers.DebugMode;
import me.calrl.hubbly.utils.ChatUtils;
import org.bukkit.event.EventHandler;
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
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

public class CompassListener implements Listener {

    private final Logger logger;
    private FileConfiguration config;
    private final NamespacedKey itemKey;
    private final Hubbly plugin;
    private final ActionManager actionManager;
    private final NamespacedKey actionsKey;
    private DebugMode debugMode;
    private boolean isBungee;
    private boolean isVelocity;


    public CompassListener(Hubbly plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.config = plugin.getServerSelectorConfig();
        this.actionManager = plugin.getActionManager();
        this.itemKey = new NamespacedKey(plugin, "compassItemKey");
        this.actionsKey = new NamespacedKey(plugin, "customActions");
        this.debugMode = plugin.getDebugMode();


        // Register the BungeeCord channel
        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, "BungeeCord");
        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, "velocity:player");
    }

    @EventHandler
    public void onPlayerRightClick(PlayerInteractEvent event) {
        if(plugin.getDisabledWorldsManager().inDisabledWorld(event.getPlayer().getWorld())) return;

        if (event.getAction() != Action.PHYSICAL) {
            Player player = event.getPlayer();
            ItemStack item = player.getInventory().getItemInMainHand();
            if (item == null || !item.hasItemMeta()) {
                return;
            }

            String materialName = config.getString("selector.material").toUpperCase();
            String itemName = config.getString("selector.name");

            if (materialName == null || itemName == null) {
                debugMode.warn("Configuration for selector.material or selector.name is missing.");
                return;
            }

            Material material;
            try {
                material = Material.valueOf(materialName.toUpperCase());
            } catch (IllegalArgumentException e) {
                debugMode.warn("Invalid material specified for selector: " + materialName);
                return;
            }

            if (item.getType() == material && ChatColor.translateAlternateColorCodes('&', itemName).equals(item.getItemMeta().getDisplayName())) {
                if (player.hasPermission("hubbly.compass.use") || player.isOp()) {
                    event.setCancelled(true);
                    openCompassGUI(player);
                } else {
                    player.sendMessage(ChatUtils.translateHexColorCodes(Objects.requireNonNull(config.getString("messages.no_permission_use"))));
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {;
        if (config == null || !config.contains("selector.gui.title")) {
            debugMode.warn("Configuration for selector.gui.title is missing.");
            return;
        }

        if (event.getView().getTitle().equals(config.getString("selector.gui.title"))) {
            event.setCancelled(true);  // Prevent item movement
            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem != null && clickedItem.hasItemMeta()) {
                ItemMeta meta = clickedItem.getItemMeta();
                PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
                String actionsString = dataContainer.get(actionsKey, PersistentDataType.STRING);

                if (actionsString != null) {
                    debugMode.info("Actions string found: " + actionsString);  // Debug log
                    String[] actions = actionsString.split(",");
                    for (String actionData : actions) {
                        actionManager.executeAction(plugin, player, actionData);
                        debugMode.info("Executing action: " + actionData);
                    }
                     debugMode.info(Arrays.toString(actions)); // Log the array contents properly
                }
            } else {
            }
        }

        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) {
            return;
        }
    }

    private void openCompassGUI(Player player) {
        if (Objects.equals(config.getString("selector.enabled"), "true")) {
            Inventory gui = Bukkit.createInventory(null, config.getInt("selector.gui.size"), Objects.requireNonNull(config.getString("selector.gui.title")));
            for (String itemKey : Objects.requireNonNull(config.getConfigurationSection("selector.gui.items")).getKeys(false)) {
                ItemStack item = createItemFromConfig(player, itemKey);
                if (item != null) {
                    int slot = config.getInt("selector.gui.items." + itemKey + ".slot");
                    if (slot >= 0 && slot < gui.getSize()) {
                        gui.setItem(slot - 1, item);
                    } else {
                        debugMode.warn("Invalid slot on item " + itemKey);
                    }
                }
            }

            if (config.isConfigurationSection("selector.fill")) {
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
        String path = "selector.gui.items." + itemKey;
        if (!config.contains(path)) {
            return null;
        }

        Material material;
        try {
            material = Material.valueOf(Objects.requireNonNull(config.getString(path + ".material")).toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            debugMode.warn("Invalid material specified for selector: " + config.getString(path + ".material"));
            return null;
        }

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            if (config.contains(path + ".name")) {
                String itemName = ChatUtils.parsePlaceholders(player, config.getString(path + ".name"));
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', itemName));
            }

            if (config.contains(path + ".lore")) {
                List<String> loreList = config.getStringList(path + ".lore");
                for (int i = 0; i < loreList.size(); i++) {
                    loreList.set(i, ChatColor.translateAlternateColorCodes('&', ChatUtils.parsePlaceholders(player, loreList.get(i))));
                }
                meta.setLore(loreList);
            }

            if (config.contains(path + ".value") && Objects.equals(material, Material.valueOf(config.getString(path + ".material")))) {
                CreateCustomHead.createCustomHead(config.getString(path + ".value"), config.getString(path + ".name"));
            }

            List<String> actions = config.getStringList(path + ".actions");
            if (!actions.isEmpty()) {
                String actionsString = String.join(",", actions);
                meta.getPersistentDataContainer().set(actionsKey, PersistentDataType.STRING, actionsString);
                debugMode.info("Set actions for item " + itemKey + ": " + actionsString);
                debugMode.info(actions.toString());
            }

            PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
            dataContainer.set(this.itemKey, PersistentDataType.STRING, itemKey);

            item.setItemMeta(meta);
        }
        return item;
    }


    private ItemStack createFillItem(Player player) {
        String materialName = config.getString("selector.fill.type");
        if (materialName == null) {
            debugMode.warn("Material not set for fill item in configuration.");
            return null;
        }

        Material material = Material.getMaterial(materialName.toUpperCase());
        if (material == null) {
            debugMode.warn("Invalid material " + materialName + " for fill item in configuration.");
            return null;
        }

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            String displayName = config.getString("selector.fill.name");
            if (displayName != null) {
                displayName = ChatUtils.parsePlaceholders(player, displayName);
                meta.setDisplayName(displayName);
            }

            item.setItemMeta(meta);
        }

        return item;
    }
}
