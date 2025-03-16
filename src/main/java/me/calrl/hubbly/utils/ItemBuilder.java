package me.calrl.hubbly.utils;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.stream.Collectors;

public class ItemBuilder {
    private ItemStack itemStack;
    private ItemMeta itemMeta;
    private Player player;

    public ItemBuilder(Material material) {
        this.itemStack = new ItemStack(material);
        this.itemMeta = itemStack.getItemMeta();
    }
    public ItemBuilder(Material material, String displayName) {
        this.itemStack = new ItemStack(material);
        this.itemMeta = itemStack.getItemMeta();
        this.setName(displayName);
    }
    public ItemBuilder(Material material, String displayName, int modelData) {
        this.itemStack = new ItemStack(material);
        this.itemMeta = itemStack.getItemMeta();
        this.setName(displayName);
        this.setCustomModelData(modelData);
    }

    /**
     *
     * @param player
     * @return
     */
    public ItemBuilder setPlayer(Player player) {
        this.player = player;
        return this;
    }

    /**
     * Sets the item display name
     * @param name
     * @return
     */
    public ItemBuilder setName(String name) {
        if (itemMeta != null && name != null) {
            boolean isPapiEnabled = Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");
            if (player != null && isPapiEnabled) {
                name = ChatUtils.processMessage(player, name);
            }
            itemMeta.setDisplayName(name);
        }
        return this;
    }

    /**
     * Sets the item lore
     * @param lore
     * @return
     */
    public ItemBuilder setLore(List<String> lore) {
        if (itemMeta != null && lore != null) {
            if (player != null && Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                lore = lore.stream().map(line -> PlaceholderAPI.setPlaceholders(player, line)).collect(Collectors.toList());
            }
            itemMeta.setLore(lore);
        }
        return this;
    }
    public ItemBuilder setCustomModelData(Integer modelData) {
        if (itemMeta != null && modelData != null) {
            itemMeta.setCustomModelData(modelData);
        }
        return this;
    }
    public ItemBuilder addPersistentData(NamespacedKey key, PersistentDataType<String, String> type, String value) {
        if (itemMeta != null && key != null && value != null) {
            PersistentDataContainer container = itemMeta.getPersistentDataContainer();
            container.set(key, type, value);
        }
        return this;
    }

    /**
     * Builds the item and returns the final ItemStack
     */
    public ItemStack build() {
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }



}
