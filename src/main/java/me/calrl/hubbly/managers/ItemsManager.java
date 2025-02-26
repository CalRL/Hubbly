package me.calrl.hubbly.managers;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.interfaces.CustomItem;
import me.calrl.hubbly.items.*;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ItemsManager {

    private Hubbly plugin;
    private FileConfiguration config;
    private FileConfiguration itemsConfig;
    private DebugMode debugMode;
    private final Map<String, CustomItem> items = new HashMap<>();
    private File itemsFile;

    public ItemsManager(Hubbly plugin) {
        this.plugin = plugin;
        this.debugMode = plugin.getDebugMode();
        this.itemsFile =  new File(plugin.getDataFolder(), "items.yml");
        this.loadConfig();
        this.registerItems();
    }

    private void registerItems() {
        items.put("compass", new CompassItem());
        items.put("socials", new SocialsItem());
        items.put("playervisibility", new PlayerVisibilityItem());
        items.put("enderbow", new EnderbowItem(plugin));
        items.put("trident", new TridentItem(plugin));
        items.put("grappling_hook", new RodItem(plugin));
        items.put("aote", new AoteItem(plugin));

        if (itemsConfig.getConfigurationSection("items") != null) {
            for (String itemKey : itemsConfig.getConfigurationSection("items").getKeys(false)) {
                items.put(ChatColor.stripColor(itemKey.toLowerCase()), new ConfigItems(itemKey, plugin));
            }
        } else {
            debugMode.warn("No items found in items.yml");
        }
    }
    private void loadConfig() {
        this.itemsConfig = YamlConfiguration.loadConfiguration(itemsFile);
    }
    public Map<String, CustomItem> getItems() {
        return items;
    }

    public FileConfiguration getConfig() {
        return itemsConfig;
    }

    public Set<String> getItemNames() {
        Set<String> itemNames = items.keySet();

        System.out.println(itemNames);
        return itemNames;
    }

    private ItemStack createItemFromConfig(String itemKey, Player player) {

    }

    private ItemMeta createMetaFromConfig(ItemMeta meta) {

    }

    public void executeActions(Player player, ItemStack item) {

    }

}
