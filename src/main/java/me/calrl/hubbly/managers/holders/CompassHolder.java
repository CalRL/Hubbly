package me.calrl.hubbly.managers.holders;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class CompassHolder implements InventoryHolder {

    private Hubbly plugin;
    public CompassHolder(Hubbly plugin) {
        this.plugin = plugin;
    }


    @NotNull
    @Override
    public Inventory getInventory() {
        ConfigurationSection section = plugin.getServerSelectorConfig().getConfigurationSection("selector");
        if(section == null) return null;

        Inventory gui = Bukkit.createInventory(this, section.getInt("gui.size", 54),
                ChatUtils.translateHexColorCodes(section.getString("gui.title", "unconfigured")));

        return gui;

    }

}
