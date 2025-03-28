package me.calrl.hubbly.managers;

import me.calrl.hubbly.Hubbly;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Path;
import java.util.*;

public class FileManager {
    private FileConfiguration serverSelectorConfig;
    private FileConfiguration itemsConfig;
    private Hubbly plugin;
    private final File baseFolder;
    private final Map<String, YamlConfiguration> loadedConfigs = new HashMap<>();
    private final Set<String> lockedFiles = new HashSet<>();
    public FileManager(Hubbly plugin) {
        this.plugin = plugin;
        this.baseFolder = plugin.getDataFolder();
    }

    public File resolve(String relativePath) {
        if (!relativePath.endsWith(".yml")) {
            relativePath += ".yml";
        }

        File file = new File(baseFolder, relativePath);
        File parent = file.getParentFile();

        if (!parent.exists()) {
            boolean mkDir = parent.mkdirs();
            if(!mkDir) plugin.getLogger().warning("Couldn't create directory: " + parent);
        }

        return file;
    }

    public void save(String relativePath) {
        YamlConfiguration config = loadedConfigs.get(relativePath);
        if (config == null) {
            plugin.getLogger().warning("Tried to save unloaded config: " + relativePath);
            return;
        }

        File file = this.resolve(relativePath);

        try {
            config.save(file);
            plugin.getLogger().info("Saved config: " + relativePath);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save config: " + relativePath);
            e.printStackTrace();
        }
    }

    public void create(String relativePath) {

    }

    public void loadFiles(String relativePath) {

    }

    public void reload(String relativePath) {
        if (lockedFiles.contains(relativePath)) {
            plugin.getLogger().info("File " + relativePath + " is locked and will not be reloaded.");
            return;
        }

        File file = this.resolve(relativePath);
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        loadedConfigs.put(relativePath, config);
        plugin.getLogger().info("Reloaded: " + relativePath);
    }

    public void reloadFiles() {
        for (String path : loadedConfigs.keySet()) {
            if (!lockedFiles.contains(path)) {
                this.reload(path);
            } else {
                plugin.getLogger().info("Skipped reload for locked file: " + path);
            }
        }
    }

    public YamlConfiguration getConfig(String relativePath) {
        if (loadedConfigs.containsKey(relativePath)) {
            return loadedConfigs.get(relativePath);
        }

        File file = this.resolve(relativePath);

        if (!file.exists()) {
            plugin.getLogger().warning("Failed to get file: " + file.getName());
            return null;
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        loadedConfigs.put(relativePath, config);
        return config;
    }

    public void lock(String relativePath) {
        lockedFiles.add(relativePath);
    }

    public void unlock(String relativePath) {
        lockedFiles.remove(relativePath);
    }

    public boolean isLocked(String relativePath) {
        return lockedFiles.contains(relativePath);
    }



}
