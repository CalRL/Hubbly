package me.calrl.hubbly.inventory;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.enums.PluginKeys;
import me.calrl.hubbly.managers.DebugMode;
import me.calrl.hubbly.utils.ChatUtils;
import me.calrl.hubbly.utils.ItemBuilder;
import me.calrl.hubbly.utils.MessageBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
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

    public String getTitle() {
        return this.title;
    }

    public int getSize() {
        return this.size;
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

    public InventoryBuilder fromLegacySocials(FileConfiguration config) {
        ConfigurationSection socials = config.getConfigurationSection("socials");
        if (socials == null) {
            Hubbly.getInstance().getLogger().warning("Missing 'socials' section in config.");
            return this;
        }

        this.size = socials.getInt("size", 27);
        this.title = socials.getString("title", "&dSocial Menu");

        ConfigurationSection items = socials.getConfigurationSection("items");
        if (items == null) {
            Hubbly.getInstance().getLogger().warning("Missing 'items' section in socials config.");
            return this;
        }

        for (String key : items.getKeys(false)) {
            ConfigurationSection itemSection = items.getConfigurationSection(key);
            if (itemSection == null) continue;

            int slot = itemSection.getInt("slot", -1);
            Material mat = Material.valueOf(itemSection.getString("material", "PLAYER_HEAD"));
            String message = itemSection.getString("message", "");
            String hover = itemSection.getString("hover", "");
            String link = itemSection.getString("link", "");

            String tag = String.format("[LINK] %s;%s;%s", message, hover, link);
            ItemStack item = new ItemBuilder(mat)
                    .setPlayer(player)
                    .setName(itemSection.getString("name", "&7Social Item"))
                    .setTextures(itemSection.getString("value", ""))
                    .addPersistentData(PluginKeys.ACTIONS_KEY.getKey(), PersistentDataType.STRING, tag)
                    .build();

            slot = slot-1;
            if (slot != -1) {
                setItem(slot, item);
            }
        }

        ConfigurationSection fillSection = socials.getConfigurationSection("fill");
        if (fillSection != null) {
            Material mat = Material.valueOf(fillSection.getString("type", "BLACK_STAINED_GLASS_PANE"));
            ItemStack fillItem = new ItemBuilder(mat)
                    .setPlayer(player)
                    .setName(fillSection.getString("name", " "))
                    .build();

            for (int i = 0; i < size; i++) {
                if (!icons.containsKey(i)) {
                    setItem(i, fillItem);
                }
            }
        }

        return this;
    }

    public InventoryBuilder fromLegacySelector(FileConfiguration config) {
        ConfigurationSection selector = config.getConfigurationSection("selector");
        if (selector == null) {
            Hubbly.getInstance().getLogger().warning("Missing 'selector' section in config.");
            return this;
        }

        ConfigurationSection guiSection = selector.getConfigurationSection("gui");
        if (guiSection == null) {
            Hubbly.getInstance().getLogger().warning("Missing 'gui' section in selector config.");
            return this;
        }
        Hubbly.getInstance().getLogger().info("Children of guiSection: " + guiSection.getKeys(false));
        this.size = guiSection.getInt("size", 27);
        this.title = guiSection.getString("title", "&6Server Selector");

        Inventory gui = Bukkit.createInventory(this, size, ChatUtils.translateHexColorCodes(title));

        ConfigurationSection items = config.getConfigurationSection("selector.gui.items");
        if (items == null) {
            Hubbly.getInstance().getLogger().warning("Missing 'items' section in selector config.");
            return this;
        }

        for (String key : items.getKeys(false)) {
            ConfigurationSection itemSection = items.getConfigurationSection(key);
            if (itemSection == null) continue;

            int slot = itemSection.getInt("slot", -1);
            String materialName = itemSection.getString("material", "STONE").toUpperCase();
            Material material = Material.matchMaterial(materialName);
            if (material == null) {
                Hubbly.getInstance().getLogger().warning("Invalid material: " + materialName);
                continue;
            }

            ItemBuilder itemBuilder = new ItemBuilder(material)
                    .setPlayer(player)
                    .setName(itemSection.getString("name", "&6Server Selector"));

            if (itemSection.contains("lore")) {
                itemBuilder.setLore(itemSection.getStringList("lore"));
            }

            if (itemSection.contains("actions")) {
                List<String> actions = itemSection.getStringList("actions");
                String actionData = String.join(",", actions);
                itemBuilder.addPersistentData(PluginKeys.ACTIONS_KEY.getKey(), PersistentDataType.STRING, actionData);
            }

            ItemStack item = itemBuilder.build();
            slot = slot-1;
            if (slot >= 0 && slot < size) {
                gui.setItem(slot, item);
            }
        }

        ConfigurationSection fillSection = selector.getConfigurationSection("fill");
        if (fillSection != null) {
            String fillMaterialName = fillSection.getString("type", "BLACK_STAINED_GLASS_PANE").toUpperCase();
            Material fillMaterial = Material.matchMaterial(fillMaterialName);
            if (fillMaterial == null) fillMaterial = Material.BLACK_STAINED_GLASS_PANE;

            ItemStack fillItem = new ItemBuilder(fillMaterial)
                    .setPlayer(player)
                    .setName(fillSection.getString("name", " "))
                    .build();

            for (int i = 0; i < size; i++) {
                ItemStack existing = gui.getItem(i);
                if (existing == null || existing.getType() == Material.AIR) {
                    gui.setItem(i, fillItem);
                }
            }
        }

        this.icons = new HashMap<>();
        for (int i = 0; i < size; i++) {
            ItemStack item = gui.getItem(i);
            if (item != null && item.getType() != Material.AIR) {
                icons.put(i, item);
            }
        }

        return this;
    }



    public InventoryBuilder handleLegacy(FileConfiguration config) {
        ConfigurationSection section = config.getConfigurationSection("legacy");

        String version = section.getString("value");

        String type = section.getString("type");

        if(type == null) {
            Hubbly.getInstance().getLogger().severe("Type is null in config: " + config.getName());
        }
        InventoryBuilder builder = new InventoryBuilder();
        if(type.equals("socials")) {
            builder = this.fromLegacySocials(config);
        } else if(type.equals("selector")) {
            builder =  this.fromLegacySelector(config);
        } else {
            //todo: add error message
            Hubbly.getInstance().getLogger().severe("Couldn't parse legacy type: " + type);
        }
        new DebugMode().warn("Legacy " + type + " detected, please update");

        return builder;
    }

    public InventoryBuilder fromFile(FileConfiguration config) {
        if(config.contains("legacy")) {
            return this.handleLegacy(config);
        }

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
            ItemStack item = new ItemBuilder().setPlayer(player).fromConfig(player, itemSection).build();

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
