package me.calrl.hubbly.managers;

import com.google.common.base.Supplier;
import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.action.ActionManager;
import me.calrl.hubbly.enums.Result;
import me.calrl.hubbly.interfaces.CustomItem;
import me.calrl.hubbly.items.*;
import me.calrl.hubbly.listeners.items.movement.AoteListener;
import me.calrl.hubbly.listeners.items.movement.EnderbowListener;
import me.calrl.hubbly.listeners.items.movement.RodListener;
import me.calrl.hubbly.listeners.items.movement.TridentListener;
import me.calrl.hubbly.service.ILifecycle;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.*;

public class ItemsManager implements ILifecycle {

    private Hubbly plugin;
    private FileConfiguration config;
    private FileConfiguration itemsConfig;
    private DebugMode debugMode;
    private final Map<String, CustomItem> items = new HashMap<>();
    private Map<String, Listener> listeners;
    private File itemsFile;
    private ActionManager actionManager;
    private Player player;

    public ItemsManager(Hubbly plugin) {
        this.plugin = plugin;
    }

    private void setConfig(FileConfiguration config) {
        this.itemsConfig = config;
    }

    private void registerItems() {
        this.debugMode.info("Registering Items");
        this.setConfig(plugin.resources().fileManager().getConfig("items.yml"));

        this.registerPlayerVisibility();

        this.register("trident", new TridentItem(plugin), new TridentListener(plugin));
        this.register("enderbow", new EnderbowItem(plugin) , new EnderbowListener(plugin));
        this.register("aote", new AoteItem(plugin), new AoteListener(plugin));
        this.register("grappling_hook", new RodItem(plugin), new RodListener(plugin));

        ConfigurationSection section = itemsConfig.getConfigurationSection("items");
        if(section == null) {
            debugMode.info("No items found in items.yml");
            return;
        }

        for (String itemKey : section.getKeys(false)) {
            this.register(itemKey);
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
        listeners.put(itemName, listener);
        debugMode.info("Registered item: " + itemName);
    }

    /**
     * Registers the player visibility item if it is enabled in config.
     */
    private void registerPlayerVisibility() {
        if(!plugin.getConfig().getBoolean("playervisibility.enabled")) {
            return;
        }

        items.put("playervisibility", new PlayerVisibilityItem());
        this.debugMode.info("Registering Items");
    }

    private void register(String itemKey) {
        this.items.put(itemKey.toLowerCase(), new ConfigItem(itemKey, plugin));
        this.debugMode.info("Registered item: " + itemKey);
    }

    private void register(String itemName, CustomItem item) {
        this.config = plugin.getConfig();
        String enabledPath = "movementitems." + itemName + ".enabled";
        boolean isEnabled = config.getBoolean(enabledPath);
        if(!isEnabled) {
            debugMode.info("Item: " + itemName + " not registered.");
            return;
        }

        items.put(itemName, item);
        debugMode.info("Registered item: " + itemName);
    }

    public void clean() {
        Collection<Listener> collection = this.listeners.values();
        for(Listener listener : collection) {
            HandlerList.unregisterAll(listener);
        }
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

        if(meta == null) {
            return null;
        }

        PersistentDataContainer container = meta.getPersistentDataContainer();
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

    @Override
    public void onEnable() {
        this.debugMode = plugin.getDebugMode();
        this.config = plugin.getConfig();
        this.itemsFile =  new File(plugin.getDataFolder(), "items.yml");
        this.actionManager = plugin.gameplay().actionManager();
        this.itemsConfig = plugin.getItemsConfig();

        this.listeners = new HashMap<>();

        this.registerItems();
    }

    @Override
    public void onReload() {
        this.clear();
        this.registerItems();
    }

    @Override
    public void onDisable() {
        this.clear();
        this.clean();
    }
}
