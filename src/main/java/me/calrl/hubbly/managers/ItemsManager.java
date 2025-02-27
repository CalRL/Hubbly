package me.calrl.hubbly.managers;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.action.Action;
import me.calrl.hubbly.action.ActionManager;
import me.calrl.hubbly.functions.CreateCustomHead;
import me.calrl.hubbly.interfaces.CustomItem;
import me.calrl.hubbly.items.*;
import me.calrl.hubbly.utils.ChatUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
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


    private void registerItems() {
        items.put("compass", new CompassItem(plugin));
        items.put("socials", new SocialsItem());
        items.put("playervisibility", new PlayerVisibilityItem());
        items.put("enderbow", new EnderbowItem(plugin));
        items.put("trident", new TridentItem(plugin));
        items.put("grappling_hook", new RodItem(plugin));
        items.put("aote", new AoteItem(plugin));

        if (itemsConfig.getConfigurationSection("items") != null) {
            for (String itemKey : itemsConfig.getConfigurationSection("items").getKeys(false)) {
                items.put(ChatColor.stripColor(itemKey.toLowerCase()), new ConfigItem(itemKey, plugin));
                debugMode.info("Registered item: " + itemKey);
            }
        } else {
            debugMode.warn("No items found in items.yml");
        }
        debugMode.warn("1");
        for(String row : itemsConfig.getStringList("")) {
            debugMode.warn(row);
        }
        debugMode.warn("2");
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
        if(actions.isEmpty()) {
            return;
        }
        for (String actionData : actions) {
            actionManager.executeAction(Hubbly.getInstance(), player, actionData);
            debugMode.info("Executing action: " + actionData);
        }

    }

}
