package me.calrl.hubbly.inventory;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.utils.ChatUtils;
import me.calrl.hubbly.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class InventoryBuilder implements InventoryHolder {
    private String title;
    private int size;
    private Map<Integer, ItemStack> icons;
    private Player player = Bukkit.getPlayer("SEKAIcal");

    public InventoryBuilder() {
        this.icons = new HashMap<>();
    }

    public InventoryBuilder(int size, String title) {
        this.icons = new HashMap<>();
        this.size = size;
        this.title = title;
    }

    public Map<Integer, ItemStack> getIcons() {
        return this.icons;
    }

    public InventoryBuilder setPlayer(Player player) {
        this.player = player;
        return this;
    }

    public void setItem(int slot, ItemStack item) {
        icons.put(slot, item);
    }

    public ItemStack getIcon(final int slot) {
        return icons.get(slot);
    }

    @Override
    public @NotNull Inventory getInventory() {
        if(size > 54) size = 54;
        if(size < 9) size = 9;

        String title = ChatUtils.translateHexColorCodes(this.title);
        Inventory inventory = Bukkit.createInventory(this, size, title);
        for(Map.Entry<Integer, ItemStack> entry : icons.entrySet()) {
            inventory.setItem(entry.getKey(), entry.getValue());
        }

        return inventory;
    }

    public InventoryBuilder fromFile(FileConfiguration config) {
        this.size = config.getInt("size", 27);
        this.title = config.getString("title", "&bMenu");

        ConfigurationSection items = config.getConfigurationSection("items");
        if (items == null) {
            Hubbly.getInstance().getLogger().warning("Missing 'items' section in config.");
            return this;
        }

        ItemStack fillItem = null;

        for (String key : items.getKeys(false)) {
            ConfigurationSection itemSection = items.getConfigurationSection(key);
            if (itemSection == null) continue;

            int slot = itemSection.getInt("slot", -1);
            ItemStack item = new ItemBuilder().setPlayer(player).fromConfig(player, itemSection);

            if (slot == -1) {
                fillItem = item;
            } else {
                setItem(slot, item);
            }
        }

        if (fillItem != null) {
            for (int i = 0; i < size; i++) {
                if (!icons.containsKey(i)) {
                    setItem(i, fillItem);
                }
            }
        }

        return this;
    }




}
