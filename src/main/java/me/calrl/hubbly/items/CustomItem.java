package me.calrl.hubbly.items;

import me.calrl.hubbly.enums.PluginKeys;
import me.calrl.hubbly.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public abstract class CustomItem {
    private String tag;
    private Material material;
    private String displayName;
    private int modelData;
    private List<String> lore;

    public CustomItem(Material material, String displayName, String tag, List<String> lore, int modelData) {
        this.material = material;
        this.tag = tag;
        this.displayName = displayName;
        this.modelData = modelData;
        this.lore = lore;
    }

    public ItemStack createItem() {
        return new ItemBuilder(material)
                .setName(displayName)
                .setLore(lore)
                .setCustomModelData(modelData)
                .addPersistentData(PluginKeys.AOTE.getKey(), PersistentDataType.STRING, tag)
                .build();
    }
}
