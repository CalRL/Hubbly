package me.calrl.hubbly.inventory;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class CustomGUI {
    private FileConfiguration config;
    private InventoryBuilder inventory;
    private final Hubbly plugin;
    private CustomGUI(Hubbly plugin, FileConfiguration config, Player player) {
        this.config = config;
        this.plugin = plugin;
        load(player);
    }

    private void load(Player player) {
        ConfigurationSection section = config.getConfigurationSection("menu");
        if (section == null) {
            plugin.getLogger().warning("Missing 'menu' section in GUI config.");
            return;
        }

        int size = section.getInt("size", 27);
        String title = section.getString("title", "&bCustom GUI");

        inventory = new InventoryBuilder(size, title);

        ConfigurationSection items = section.getConfigurationSection("items");
        if (items == null) return;

        ItemStack fillItem = null;

        for (String key : items.getKeys(false)) {
            ConfigurationSection itemSection = items.getConfigurationSection(key);
            if (itemSection == null) continue;

            int slot = itemSection.getInt("slot", -1);
            ItemStack item = new ItemBuilder()
                    .fromConfig(player, itemSection)
                    .build();

            if (slot == -1) {
                fillItem = item;
            } else {
                if (slot >= 0 && slot < size) {
                    inventory.setItem(slot, item);
                } else {
                    plugin.getLogger().warning("Invalid slot for item '" + key + "': " + slot);
                }
            }
        }

        if (fillItem != null) {
            for (int i = 0; i < size; i++) {
                if (inventory.getInventory().getItem(i) == null || inventory.getInventory().getItem(i).getType() == Material.AIR) {
                    inventory.setItem(i, fillItem);
                }
            }
        }
    }

    public Inventory getInventory() {
        return this.inventory.getInventory();
    }


}
