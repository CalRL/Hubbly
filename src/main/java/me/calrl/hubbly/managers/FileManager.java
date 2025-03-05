package me.calrl.hubbly.managers;

import me.calrl.hubbly.Hubbly;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

public class FileManager {
    private FileConfiguration serverSelectorConfig;
    private FileConfiguration itemsConfig;
    private Hubbly plugin;
    public FileManager(Hubbly plugin) {
        this.plugin = plugin;
        loadFiles();
    }

    private void loadFiles() {
        File itemsFile = new File(plugin.getDataFolder(), "items.yml");
        if(!itemsFile.exists()) {
            plugin.saveResource("items.yml", false);
        }
        itemsConfig = YamlConfiguration.loadConfiguration(itemsFile);

        File serverSelectorFile = new File(plugin.getDataFolder(), "serverselector.yml");
        if(!serverSelectorFile.exists()) {
            plugin.saveResource("serverselector.yml", false);
        }
        serverSelectorConfig = YamlConfiguration.loadConfiguration(serverSelectorFile);
    }

    public FileConfiguration getItemsConfig() {
        return itemsConfig;
    }
    public FileConfiguration getServerSelectorConfig() {
        return serverSelectorConfig;
    }


}
