package com.caldev.listeners;

import com.caldev.functions.CreateCustomHead;
import com.caldev.functions.ParsePlaceholders;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

public class SocialsListener implements Listener {

    private Logger logger;
    private FileConfiguration config;
    public SocialsListener(Logger logger, FileConfiguration config) {
        this.logger = logger;
        this.config = config;
    }

    @EventHandler
    private void onPlayerInteract(PlayerInteractEvent event) {
        if(event.getAction() != Action.PHYSICAL && event.getPlayer().getInventory().getItemInMainHand().getType() == Material.valueOf(config.getString("socials.item.type"))) {
            ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
            if (item.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString("socials.item.name"))))) {
                if(event.getPlayer().hasPermission("hubbly." + "socials.use") || event.getPlayer().isOp()) {
                    openSocialsGUI(event.getPlayer());
                }
            }
        }
    }

    @EventHandler
    private void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        ItemStack heldItem = player.getInventory().getItemInMainHand();
        if(heldItem.getType() == Material.PLAYER_HEAD && heldItem.getItemMeta().getDisplayName().equals(Objects.requireNonNull(ChatColor.translateAlternateColorCodes('&', config.getString("socials.item.name"))))) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onInventoryClick(InventoryClickEvent event) {
        ItemStack clickedItem = event.getCurrentItem();
        if(clickedItem == null || clickedItem.getType() == Material.AIR) {
            return;
        }
        InventoryView view = event.getView();
        if(!view.getTitle().equals(Objects.requireNonNull(config.getString("socials.title")))) {
            return;
        }

        event.setCancelled(true);

        ItemMeta meta = clickedItem.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) {
            return;
        }

        if(clickedItem.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString("socials.items.close.name"))))) {
            event.getView().close();
        }

        Player player = (Player) event.getWhoClicked();
        String clickedDisplayName = meta.getDisplayName();

        for (String itemKey : Objects.requireNonNull(config.getConfigurationSection("socials.items")).getKeys(false)) {
            String itemDisplayName = ParsePlaceholders.parsePlaceholders(player, ChatColor.translateAlternateColorCodes('&', config.getString("socials.items." + itemKey + ".name")));
            if (itemDisplayName != null && itemDisplayName.equals(clickedDisplayName)) {
                String message = config.getString("socials.items." + itemKey + ".message");
                if (message != null) {
                    message = ParsePlaceholders.parsePlaceholders(player, message);
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                }
                break;
            }
        }
    }

    private void openSocialsGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, config.getInt("socials.size"), Objects.requireNonNull(config.getString("socials.title")));
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
            displayName = ParsePlaceholders.parsePlaceholders(player, displayName);
            //logger.info("Creating custom head with texture value: " + textureValue + " and display name: " + displayName);
            item = CreateCustomHead.createCustomHead(textureValue, displayName);
        } else {
            item = new ItemStack(material);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                String displayName = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString("socials.items." + itemKey + ".name")));
                if (displayName != null) {
                    displayName = ParsePlaceholders.parsePlaceholders(player, displayName);
                    meta.setDisplayName(displayName);
                }

                List<String> lore = config.getStringList("socials.items." + itemKey + ".lore");
                if (lore != null && !lore.isEmpty()) {
                    for (int i = 0; i < lore.size(); i++) {
                        lore.set(i, ParsePlaceholders.parsePlaceholders(player, lore.get(i)));
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
