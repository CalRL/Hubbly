package me.calrl.hubbly.items;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.enums.PluginKeys;
import me.calrl.hubbly.interfaces.CustomItem;
import me.calrl.hubbly.utils.ChatUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class RodItem implements CustomItem {
    private Hubbly plugin;
    public RodItem(Hubbly plugin) {
        this.plugin = plugin;
    }

    @Override
    public ItemStack createItem() {
        ItemStack item = new ItemStack(Material.FISHING_ROD);
        ItemMeta meta = item.getItemMeta();
        if(meta != null) {
            meta.getPersistentDataContainer().set(PluginKeys.FISHING_ROD.getKey(), PersistentDataType.STRING, "rod");
            meta.setDisplayName(ChatUtils.translateHexColorCodes(plugin.getConfig().getString("movementitems.fishing_rod.name")));
        }
        item.setItemMeta(meta);
        return item;
    }
}
