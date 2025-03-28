package me.calrl.hubbly.inventory;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.enums.PluginKeys;
import me.calrl.hubbly.managers.DebugMode;
import me.calrl.hubbly.managers.holders.CompassHolder;
import me.calrl.hubbly.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Logger;

public class CompassInventory {
    private Hubbly plugin;
    private DebugMode debugMode;

    private NamespacedKey actionsKey;
    private Player player;
    private Logger logger;


    public CompassInventory(Hubbly plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        this.setActionsKey();
        this.debugMode = plugin.getDebugMode();
        this.logger = plugin.getLogger();
    }

    public Inventory getInventory() {
        FileConfiguration config = plugin.getServerSelectorConfig();
        Inventory gui = new CompassHolder(plugin).getInventory();

        this.addItems(gui, config);

        if (config.isConfigurationSection("selector.fill")) {
            ConfigurationSection section = config.getConfigurationSection("selector.fill");
            if(section == null) {
                debugMode.warn("CompassInventory: Section is null");
                return null;
            }

            this.addFillItems(gui, section);
        }
        return gui;
    }
    public void addItems(Inventory gui, FileConfiguration config) {
        Set<String> section = config.getConfigurationSection("selector.gui.items").getKeys(false);
        if (section.isEmpty()) {
            logger.warning("CompassInventory: Section is null");
        }

        for (String itemKey : Objects.requireNonNull(config.getConfigurationSection("selector.gui.items")).getKeys(false)) {
            ItemStack item = createItemFromConfig(itemKey);
            if (item != null) {
                int slot = config.getInt("selector.gui.items." + itemKey + ".slot");
                if (slot >= 0 && slot < gui.getSize()) {
                    gui.setItem(slot - 1, item);
                } else {
                    logger.warning("Invalid slot on item " + itemKey);
                }
            }
        }

    }

    public void addFillItems(Inventory gui, ConfigurationSection section) {
        ItemStack fillItem = this.getFillItem(section);

        for (int i = 0; i < gui.getSize(); i++) {
            ItemStack item = gui.getItem(i);
            if (item == null || item.getType() == Material.AIR) {
                gui.setItem(i, fillItem);
            }
        }

    }

    private ItemStack getFallbackItem() {
        return new ItemBuilder(Material.STONE)
                .setPlayer(player)
                .setName("FALLBACK ITEM")
                .addGlow()
                .build();
    }
    private ItemStack getFillItem(ConfigurationSection section) {
        String materialName = section.getString("type", "BARRIER");
        Material material = Material.valueOf(materialName.toUpperCase());

        return new ItemBuilder(material)
                .setPlayer(player)
                .setName(section.getString("name"))
                .build();


    }

    public ItemStack createItemFromConfig(String itemKey) {
        String path = "selector.gui.items." + itemKey;

        FileConfiguration selectorConfig = plugin.getServerSelectorConfig();
        if(!selectorConfig.contains(path)) {
            debugMode.warn("CompassInventory: Path not found");
            return this.getFallbackItem();
        }
        ConfigurationSection section = selectorConfig.getConfigurationSection(path);
        if(section == null) {
            debugMode.warn("CompassInventory: Section is null");
            return this.getFallbackItem();
        }

        Material material;
        String materialValue = section.getString("material", "STONE");
        material = Material.valueOf(materialValue.toUpperCase());

        List<String> actions = this.getActions(section);
        String actionData = String.join(",", actions);

        return new ItemBuilder(material)
                .fromConfig(player, section);
    }

    private List<String> getActions(ConfigurationSection section) {
        return section.getStringList("actions");
    }
    private NamespacedKey getActionsKey() {
        return actionsKey;
    }

    public void setActionsKey() {
        this.actionsKey = PluginKeys.ACTIONS_KEY.getKey();
    }
}
