/*
 * This file is part of Hubbly.
 *
 * Hubbly is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Hubbly is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Hubbly. If not, see <http://www.gnu.org/licenses/>.
 */

package me.calrl.hubbly;

import me.calrl.hubbly.commands.*;
import me.calrl.hubbly.listeners.*;
import me.calrl.hubbly.metrics.Metrics;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

public final class Hubbly extends JavaPlugin {

    private final Logger logger = getLogger();
    private FileConfiguration config;
    private static Hubbly instance;

    public void reloadPlugin() {
        this.reloadConfig();
        this.saveConfig();

        config = this.getConfig();
        try {
            logger.info("Loading Components");
            loadComponents();
            logger.info("Loaded!");
        } catch(Error e) {
            e.printStackTrace();
        }

    }

    public void loadComponents() {
        getServer().getPluginManager().registerEvents(new CompassListener(logger, this), this);
        getServer().getPluginManager().registerEvents(new PlayerVisibilityListener(this), this);
        getServer().getPluginManager().registerEvents(new LaunchpadListener(logger, this), this);
        getServer().getPluginManager().registerEvents(new ShopListener(logger), this);
        getServer().getPluginManager().registerEvents(new ItemJoinListener(logger, this), this);
        getServer().getPluginManager().registerEvents(new SocialsListener(logger),this);
        getServer().getPluginManager().registerEvents(new VoidDamageListener(logger), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(logger), this);
        getServer().getPluginManager().registerEvents(new WorldEventListeners(logger), this);

        getCommand("hubbly").setExecutor(new HubblyCommand(logger, this));
        getCommand("setspawn").setExecutor(new SetSpawnCommand(this));
        getCommand("spawn").setExecutor(new SpawnCommand(this));
        getCommand("fly").setExecutor(new FlyCommand(logger));
    }
    @Override
    public void onEnable() {
        instance = this;
        // Plugin startup logic
//        if (!new File(getDataFolder(), "config.yml").exists()) {
//            saveDefaultConfig();
//        }
        this.saveDefaultConfig();
        loadComponents();
        logger.info("Hubbly has been enabled!");



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
    public static Hubbly getInstance() {
        return instance;
    }
}
