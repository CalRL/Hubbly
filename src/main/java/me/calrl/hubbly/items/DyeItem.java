package me.calrl.hubbly.items;

import me.calrl.hubbly.enums.PluginKeys;
import me.calrl.hubbly.managers.DebugMode;
import me.calrl.hubbly.utils.ChatUtils;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class DyeItem {
    private final String key;

    public DyeItem(String key) {
        this.key = key.toLowerCase();
    }

    public String getKey() {
        return this.key;
    }

    public ItemStack build(FileConfiguration config) {
        String itemFormatted = String.format("playervisibility.%s.%s", key, "item");
        String textFormatted = String.format("playervisibility.%s.%s", key, "text");

        Material material = Material.valueOf(
                config.getString(itemFormatted)
        );

        String displayName = ChatUtils.translateHexColorCodes(
                config.getString(textFormatted)
        );

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(displayName);
            PersistentDataContainer container = meta.getPersistentDataContainer();
            container.set(PluginKeys.PLAYER_VISIBILITY.getKey(), PersistentDataType.STRING, this.key.toUpperCase());
            item.setItemMeta(meta);
        }
        return item;
    }
}
