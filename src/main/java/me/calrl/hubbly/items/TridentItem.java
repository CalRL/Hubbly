package me.calrl.hubbly.items;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.enums.PluginKeys;
import me.calrl.hubbly.interfaces.CustomItem;
import me.calrl.hubbly.utils.ChatUtils;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class TridentItem implements CustomItem {
    private FileConfiguration config;
    private Hubbly plugin;
    public TridentItem(Hubbly plugin) {
        this.plugin = plugin;
    }
    @Override
    public ItemStack createItem() {
        config = plugin.getConfig();
        ItemStack item = new ItemStack(Material.TRIDENT);
        ItemMeta meta = item.getItemMeta();
        if(meta != null) {
            PersistentDataContainer container = meta.getPersistentDataContainer();
            container.set(PluginKeys.TRIDENT.getKey(), PersistentDataType.STRING, "trident");
            String itemName = ChatUtils.translateHexColorCodes(config.getString("movementitems.trident.name", "<#89CFF0>Trident"));
            meta.setDisplayName(itemName);
            item.setItemMeta(meta);
        }
        return item;
    }
}
