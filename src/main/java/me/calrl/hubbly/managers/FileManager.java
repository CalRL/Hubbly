package me.calrl.hubbly.managers;

import me.calrl.hubbly.Hubbly;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Path;
import java.util.List;

public class FileManager {
    private FileConfiguration serverSelectorConfig;
    private FileConfiguration itemsConfig;
    private Hubbly plugin;
    private Path path;
    public FileManager(Hubbly plugin) {
        this.plugin = plugin;

        path = Path.of(plugin.getDataFolder() + "/items/");

        File directory = path.toFile();
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    public File createFile(String name) {
        return new File(path.toFile() + name);
    }

    public synchronized void saveItemStack(ItemStack itemStack, File file) {
        YamlConfiguration config = new YamlConfiguration();
        config.set("item", itemStack);

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized ItemStack loadItemStack(File file) {
        if (!file.exists()) return null;

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        return config.getItemStack("item");
    }

    public Path getPath() {
        return this.path;
    }


}
