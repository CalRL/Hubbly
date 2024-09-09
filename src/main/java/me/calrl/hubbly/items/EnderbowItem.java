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

public class EnderbowItem implements CustomItem {
    private Hubbly plugin;

    public EnderbowItem(Hubbly plugin) {
        this.plugin = plugin;
    }
    @Override
    public ItemStack createItem() {
        FileConfiguration config = plugin.getConfig();
        ItemStack item = new ItemStack(Material.BOW);
        ItemMeta meta = item.getItemMeta();

        if(meta != null) {
            PersistentDataContainer container = meta.getPersistentDataContainer();
            container.set(PluginKeys.ENDER_BOW.getKey(), PersistentDataType.STRING, "enderbow");
            String itemName = ChatUtils.translateHexColorCodes(config.getString("movementitems.enderbow.name", "<#A020F0>Enderbow"));
            meta.setDisplayName(itemName);
            item.setItemMeta(meta);
        }
        return item;
    }
}
