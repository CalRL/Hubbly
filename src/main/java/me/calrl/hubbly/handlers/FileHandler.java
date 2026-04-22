package me.calrl.hubbly.handlers;

import me.calrl.hubbly.Hubbly;

import java.io.File;

public class FileHandler {
    public FileHandler() {}

    public void saveResourceIfNotExists(Hubbly plugin, String path) {
        File file = new File(plugin.getDataFolder(), path);
        if(!file.exists()) {
            plugin.saveResource(path, false);
        }
    }
}
