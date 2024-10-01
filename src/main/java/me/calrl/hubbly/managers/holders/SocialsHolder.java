package me.calrl.hubbly.managers.holders;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class SocialsHolder implements InventoryHolder {

    private Hubbly plugin;
    public SocialsHolder(Hubbly plugin) {
        this.plugin = plugin;
    }


    @NotNull
    @Override
    public Inventory getInventory() {
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("socials");
        if(section == null) return null;

        Inventory gui = Bukkit.createInventory(this, section.getInt("size", 54),
                ChatUtils.translateHexColorCodes(section.getString("title", "unconfigured")));

        return gui;

    }

}
