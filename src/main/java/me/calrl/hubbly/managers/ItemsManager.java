package me.calrl.hubbly.managers;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.interfaces.CustomItem;
import me.calrl.hubbly.items.*;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ItemsManager {

    private Hubbly plugin;
    private FileConfiguration config;
    private FileConfiguration itemsConfig;
    private DebugMode debugMode;
    private final Map<String, CustomItem> items = new HashMap<>();

    public ItemsManager(Hubbly plugin) {
        this.plugin = plugin;
        this.debugMode = plugin.getDebugMode();
        registerItems();
    }

    private void registerItems() {
        itemsConfig = plugin.getItemsConfig();

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

    public Map<String, CustomItem> getItems() {

        return items;
    }

    public Set<String> getItemNames() {
        Set<String> itemNames = items.keySet();

        System.out.println(itemNames);
        return itemNames;
    }
}
