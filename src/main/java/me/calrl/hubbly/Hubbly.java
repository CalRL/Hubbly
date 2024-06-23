package me.calrl.hubbly;

import me.calrl.hubbly.commands.*;
import me.calrl.hubbly.listeners.*;
import me.calrl.hubbly.metrics.Metrics;
import me.calrl.hubbly.metrics.Metrics.CustomChart;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

public final class Hubbly extends JavaPlugin {

    private final Logger logger = getLogger();
    @Override
    public void onEnable() {
        // Plugin startup logic
//        if (!new File(getDataFolder(), "config.yml").exists()) {
//            saveDefaultConfig();
//        }
        this.saveDefaultConfig();
        logger.info("Hubbly has been enabled!");
        getServer().getPluginManager().registerEvents(new CompassListener(logger, getConfig(), this), this);
        getServer().getPluginManager().registerEvents(new PlayerVisibilityListener(this, getConfig()), this);
        getServer().getPluginManager().registerEvents(new LaunchpadListener(logger, this, getConfig()), this);
        getServer().getPluginManager().registerEvents(new ShopListener(logger, getConfig()), this);
        getServer().getPluginManager().registerEvents(new ItemJoinListener(logger, getConfig(), this), this);
        getServer().getPluginManager().registerEvents(new SocialsListener(logger, getConfig()), this);
        getServer().getPluginManager().registerEvents(new VoidDamageListener(logger, getConfig()), this);

        getCommand("hubbly").setExecutor(new HubblyCommand(logger, getConfig(), this));
        getCommand("setspawn").setExecutor(new SetSpawnCommand(this, getConfig()));
        getCommand("spawn").setExecutor(new SpawnCommand(this, getConfig()));


        int pluginId = 22219;
        Metrics metrics = new Metrics(this, pluginId);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        logger.info("Hubbly has been disabled!");
    }

//    public static Hubbly getInstance() {
//        return getPlugin(Hubbly.class);
//    }
    @Override
    public @NotNull FileConfiguration getConfig() {
        return super.getConfig();
    }
}
