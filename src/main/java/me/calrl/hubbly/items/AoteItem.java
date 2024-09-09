package me.calrl.hubbly.items;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.enums.PluginKeys;
import me.calrl.hubbly.interfaces.CustomItem;
import me.calrl.hubbly.utils.ChatUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class AoteItem implements CustomItem {

    private final Hubbly plugin;
    public AoteItem(Hubbly plugin) {
        this.plugin = plugin;
    }
    @Override
    public ItemStack createItem() {
        ItemStack item = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta meta = item.getItemMeta();
        if(meta != null) {
            meta.setDisplayName(
                    ChatUtils.translateHexColorCodes(
                    plugin.getConfig().getString("movementitems.aote.name", "AOTE")));
            meta.getPersistentDataContainer().set(PluginKeys.AOTE.getKey(), PersistentDataType.STRING, "aote");
        }
        item.setItemMeta(meta);
        return item;
    }
}
