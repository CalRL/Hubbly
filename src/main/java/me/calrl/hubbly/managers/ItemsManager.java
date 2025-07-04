package me.calrl.hubbly.managers;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.action.ActionManager;
import me.calrl.hubbly.interfaces.CustomItem;
import me.calrl.hubbly.items.*;
import me.calrl.hubbly.listeners.items.movement.AoteListener;
import me.calrl.hubbly.listeners.items.movement.EnderbowListener;
import me.calrl.hubbly.listeners.items.movement.RodListener;
import me.calrl.hubbly.listeners.items.movement.TridentListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.*;

public class ItemsManager {

    private Hubbly plugin;
    private FileConfiguration config;
    private FileConfiguration itemsConfig;
    private DebugMode debugMode;
    private final Map<String, CustomItem> items = new HashMap<>();
    private File itemsFile;
    private final ActionManager actionManager;
    private Player player;

    public ItemsManager(Hubbly plugin) {
        this.plugin = plugin;
        this.debugMode = plugin.getDebugMode();
        this.itemsFile =  new File(plugin.getDataFolder(), "items.yml");
        this.actionManager = plugin.getActionManager();
        this.itemsConfig = plugin.getItemsConfig();
        this.registerItems();
    }

    private void setConfig(FileConfiguration config) {
        this.itemsConfig = config;
    }

    private void registerItems() {
        this.setConfig(plugin.getFileManager().getConfig("items.yml"));
        items.put("playervisibility", new PlayerVisibilityItem());

        this.register("trident", new TridentItem(plugin), new TridentListener(plugin));
        this.register("enderbow", new EnderbowItem(plugin) , new EnderbowListener(plugin));
        this.register("aote", new AoteItem(plugin), new AoteListener(plugin));
        this.register("grappling_hook", new RodItem(plugin), new RodListener(plugin));

        if (itemsConfig.getConfigurationSection("items") != null) {
            ConfigurationSection section = itemsConfig.getConfigurationSection("items");
            if(section == null) {
                throw new NullPointerException("items key not found in the items config");
            }

            for (String itemKey : section.getKeys(false)) {
                items.put(ChatColor.stripColor(itemKey.toLowerCase()), new ConfigItem(itemKey, plugin));
                debugMode.info("Registered item: " + itemKey);
            }
        } else {
            debugMode.warn("No items found in items.yml");
        }
        for(String row : itemsConfig.getStringList("")) {
            debugMode.warn(row);
        }


    }

    private void register(String itemName, CustomItem item, Listener listener) {
        this.config = plugin.getConfig();
        String enabledPath = "movementitems." + itemName + ".enabled";
        boolean isEnabled = config.getBoolean(enabledPath);
        if(!isEnabled) {
            debugMode.info("Item: " + itemName + " not registered.");
            return;
        }

        Bukkit.getPluginManager().registerEvents(listener, plugin);
        items.put(itemName, item);
        debugMode.info("Registered item: " + itemName);
    }

    public void reload() {
        this.clear();
        this.registerItems();
    }

    public void clear() {
        items.clear();
    }

    public Map<String, CustomItem> getItems() {
        return items;
    }

    public Set<String> getItemNames() {
        Set<String> itemNames = items.keySet();

        System.out.println(itemNames);
        return itemNames;
    }

    public List<String> getActions(ItemStack item) {

        if (item == null || !item.hasItemMeta()) {
            return null;
        }
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        if(container == null) {
            return null;
        }
        String actionsString = container.get(new NamespacedKey(plugin, "customActions"), PersistentDataType.STRING);

        if (actionsString == null || actionsString.isEmpty()) {
            return null;
        }

        return Arrays.asList(actionsString.split(","));
    }

    public void executeActions(Player player, ItemStack item) {
        List<String> actions = getActions(item);
        if(actions == null || actions.isEmpty()) {
            return;
        }
        actionManager.executeActions(player, actions);

    }

}
