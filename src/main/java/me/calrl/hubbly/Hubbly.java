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

import me.calrl.hubbly.action.ActionManager;
import me.calrl.hubbly.managers.BossBarManager;
import me.calrl.hubbly.hooks.HookManager;
import me.calrl.hubbly.managers.*;
import me.calrl.hubbly.managers.cooldown.CooldownManager;
import me.calrl.hubbly.managers.LockChat;
import me.calrl.hubbly.metrics.CustomMetrics;
import me.calrl.hubbly.metrics.Metrics;
import me.calrl.hubbly.managers.StorageManager;
import me.calrl.hubbly.service.GameplayService;
import me.calrl.hubbly.service.Services;
import me.calrl.hubbly.utils.AntiWDLSetup;
import me.calrl.hubbly.utils.update.UpdateUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Logger;

public class Hubbly extends JavaPlugin {
    
    private final Logger logger = getLogger();
    private static Hubbly instance;
    private FileConfiguration itemsConfig;
    private ActionManager actionManager;
    private DebugMode debugMode;
    private AnnouncementsManager announcementsManager;
    private FileManager fileManager;
    private LocaleManager localeManager;
    private HookManager hookManager;
    private StorageManager storageManager = null;
    private Services services;
    private GameplayService gameplayService;

    private String prefix;

    private UpdateUtil updateUtil = null;

    public void reloadPlugin() {
        debugMode.info("Restarting...");

        this.reloadConfig();
        this.saveConfig();

        fileManager.reloadFiles();
        localeManager.reload();

        services().onReload();


        try {
//            cleanup();
            loadFiles();
        } catch(Exception e) {
            e.printStackTrace();
        }
        debugMode.info("Restarted.");
    }




    @Override
    public void onEnable() {
        logger.info("Starting Hubbly...");
        this.saveDefaultConfig();
        try {
            loadFiles();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        instance = this;

        if(this.getConfig().getBoolean("database.enabled")) {
            storageManager = new StorageManager(this);
        }

        fileManager = new FileManager(this);
        debugMode = new DebugMode();

        Services services = new Services(this);
        services.onEnable();
        this.services = services;

        actionManager = new ActionManager(this);

        this.gameplayService = new GameplayService(this);
        this.gameplayService.onEnable();

        announcementsManager = new AnnouncementsManager(this);
        localeManager = new LocaleManager(this);

        new CommandRegistrar(this);
        new ListenerRegistrar(this);

        logger.info("Instances created");

        prefix = this.getConfig().getString("prefix");

        logger.info("Components loaded");

        if (!this.isTestEnvironment()) {
            logger.info("Plugin is not in test mode");
            new CustomMetrics(this);

            this.services().updateUtil().checkForUpdate(this);

            this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
            debugMode.info("BungeeCord channel registered");
        }

        logger.info("Hubbly has been enabled!");
    }

    @Override
    public void onDisable() {
        logger.info("Disabling Hubbly");
        // Plugin shutdown logic
        cleanup();
        logger.info("Cleanup success");

        if(this.storageManager != null && this.storageManager.isActive()) {
            this.storageManager.shutdown();
        }

        this.gameplayService.bossBarManager().removeAllBossBars();

        this.getServer().getMessenger().unregisterOutgoingPluginChannel(this, "BungeeCord");
        this.getServer().getMessenger().unregisterIncomingPluginChannel(this, "wdl:init");
        this.getServer().getMessenger().unregisterOutgoingPluginChannel(this, "wdl:control");

        Bukkit.getScheduler().cancelTasks(this);

        logger.info("Hubbly has been disabled!");
    }


    public static Hubbly getInstance() {
        return instance;
    }

    private void loadFiles() {
        File itemsFile = new File(getDataFolder(), "items.yml");
        if(!itemsFile.exists()) {
            saveResource("items.yml", false);
        }
        itemsConfig = YamlConfiguration.loadConfiguration(itemsFile);

        /*
        todo: remove this asap
         */
        saveResourceIfNotExists("menus/selector.yml");
        saveResourceIfNotExists("menus/socials.yml");
        saveResourceIfNotExists("languages/en.yml");

    }

    public void saveResourceIfNotExists(String path) {
        File file = new File(this.getDataFolder(), path);
        if(!file.exists()) {
            saveResource(path, false);
        }
    }

    private void cleanup() {
        HandlerList.unregisterAll(this);
        Bukkit.getScheduler().cancelTasks(this);
    }


    public FileConfiguration getItemsConfig() {
        return itemsConfig;
    }
    public ActionManager getActionManager() {
        return actionManager;
    }
    public DebugMode getDebugMode() {
        return debugMode;
    }
    public AnnouncementsManager getAnnouncementsManager() {
        return announcementsManager;
    }
    public FileManager getFileManager() { return fileManager; }
    public LocaleManager getLocaleManager() {
        return localeManager;
    }
    public HookManager getHookManager() {return this.hookManager;}
    public void setHookManager(HookManager hookManager) {
        this.hookManager = hookManager;
    }
    public StorageManager getStorageManager() { return this.storageManager; }
    public static void setInstance(Hubbly hubbly) {
        instance = hubbly;
    }
    public Services services() { return this.services; }
    public GameplayService gameplay() { return this.gameplayService; }

    private boolean isTestEnvironment() {
        return Boolean.getBoolean("hubbly.test");
    }

}
