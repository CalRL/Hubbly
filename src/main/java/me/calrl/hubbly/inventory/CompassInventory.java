package me.calrl.hubbly.inventory;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.enums.PluginKeys;
import me.calrl.hubbly.managers.DebugMode;
import me.calrl.hubbly.managers.holders.CompassHolder;
import me.calrl.hubbly.utils.ChatUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.Objects;

import static me.calrl.hubbly.functions.CreateCustomHead.setTextures;

public class CompassInventory {
    private Hubbly plugin;
    private DebugMode debugMode;

    private NamespacedKey actionsKey;
    private Player player;


    public CompassInventory(Hubbly plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        this.setActionsKey();
        this.debugMode = plugin.getDebugMode();
    }

    public Inventory getInventory() {
        FileConfiguration config = plugin.getServerSelectorConfig();
        Inventory gui = new CompassHolder(plugin).getInventory();


        for (String itemKey : Objects.requireNonNull(config.getConfigurationSection("selector.gui.items")).getKeys(false)) {
            ItemStack item = createItemFromConfig(itemKey);
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
            ConfigurationSection section = config.getConfigurationSection("selector.fill");
            if(section == null) {
                debugMode.warn("CompassInventory: Section is null");
                return null;
            }

            ItemStack fillItem = this.getFillItem(section);
            for (int i = 0; i < gui.getSize(); i++) {
                ItemStack item = gui.getItem(i);
                if (item == null || item.getType() == Material.AIR) {
                    gui.setItem(i, fillItem);
                }
            }
        }
        return gui;
    }

    private ItemStack getFallbackItem() {
        ItemStack item = new ItemStack(Material.STONE);
        ItemMeta meta = item.getItemMeta();
        if(meta == null) {
            return item;
        }

        meta.setDisplayName("FALLBACK ITEM");
        item.setItemMeta(meta);
        return item;
    }
    private ItemStack getFillItem(ConfigurationSection section) {
        String materialName = section.getString("type", "BARRIER");

        Material material = Material.valueOf(materialName.toUpperCase());
        if(material == null) {
            debugMode.warn("CompassInventory: Material is null");
            return this.getFallbackItem();
        }

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if(meta == null) {
            debugMode.warn("CompassInventory: Meta is null");
            return this.getFallbackItem();
        }

        String displayName = section.getString("name", " ");
        displayName = ChatUtils.parsePlaceholders(player, displayName);
        meta.setDisplayName(displayName);

        item.setItemMeta(meta);

        return item;


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

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        item.setItemMeta(this.createMeta(section, meta, itemKey));
        if(item.getType() == Material.PLAYER_HEAD) {
            item.setItemMeta(setTextures(item, section.getString("value")));
        }

        return item;
    }

    private ItemMeta createMeta(ConfigurationSection section, ItemMeta meta, String itemKey) {
        if(section.contains("name")) {
            String itemName = this.getName(section);
            itemName = ChatUtils.parsePlaceholders(player, itemName);
            meta.setDisplayName(itemName);
        }

        if(section.contains("lore")) {
            List<String> lore = this.getLore(section);
            lore.replaceAll(message ->
                ChatUtils.parsePlaceholders(player, message)
            );
            meta.setLore(lore);
        }
        PersistentDataContainer container = meta.getPersistentDataContainer();
        if(section.contains("actions")) {
            List<String> actions = this.getActions(section);
            String actionData = String.join(",", actions);
            container.set(this.getActionsKey(), PersistentDataType.STRING, actionData);
            plugin.getDebugMode().info(String.format("Set actions: %s for item", actions));
        }

        PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
        dataContainer.set(PluginKeys.SELECTOR.getKey(), PersistentDataType.STRING, itemKey);

        return meta;
    }

    private String getName(ConfigurationSection section) {
        return section.getString("name");
    }
    private List<String> getLore(ConfigurationSection section) {
        return section.getStringList("lore");
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
