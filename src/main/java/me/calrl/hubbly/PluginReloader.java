package me.calrl.hubbly;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Optional;

public final class PluginReloader {

    private enum ValidateResult {
        Success,
        FileNotFound,
        IOError,
        InvalidConfig
    }
    private record ValidateRecord(ValidateResult result, String message) {}

    private static ValidateRecord validate(Hubbly plugin) {
        File configFile = new File(plugin.getDataFolder(), "config.yml");
        YamlConfiguration tempConfig = new YamlConfiguration();

        try {
            tempConfig.load(configFile);
        } catch (FileNotFoundException e) {
            return new ValidateRecord(ValidateResult.FileNotFound, e.getMessage());
        } catch (IOException e) {
            return new ValidateRecord(ValidateResult.IOError, e.getMessage());
        } catch (InvalidConfigurationException e) {
            return new ValidateRecord(ValidateResult.InvalidConfig, e.getMessage());
        }
        return new ValidateRecord(ValidateResult.Success, null);
    }

    public static boolean reload(Hubbly plugin) {
        ValidateRecord record = PluginReloader.validate(plugin);
        switch (record.result) {
            case Success -> {
                plugin.reloadConfig();

                plugin.fileManager().onReload();
                plugin.resources().onReload();
                plugin.services().onReload();
                plugin.gameplay().onReload();
                return true;
            }
            case FileNotFound -> {
                plugin.getLogger().severe("Couldn't find file config.yml");
            }
            case IOError -> {
                plugin.getLogger().severe("Failed to open config.yml");
            }
            case InvalidConfig -> {
                plugin.getLogger().severe("config.yml contains invalid YAML.");
                plugin.getLogger().severe("The previous configuration remains active.");
            }
        }

        if (record.message() != null) {
            plugin.getLogger().severe(record.message());
        }

        return false;
    }
}
