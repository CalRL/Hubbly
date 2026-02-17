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
import me.calrl.hubbly.listeners.world.AntiWDL;
import me.calrl.hubbly.managers.*;
import me.calrl.hubbly.managers.cooldown.CooldownManager;
import me.calrl.hubbly.managers.LockChat;
import me.calrl.hubbly.metrics.Metrics;
import me.calrl.hubbly.managers.StorageManager;
import me.calrl.hubbly.service.Services;
import me.calrl.hubbly.utils.Utils;
import me.calrl.hubbly.utils.update.UpdateUtil;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.logging.Logger;

public class Hubbly extends JavaPlugin {

    private static boolean testMode = false;
    private final Logger logger = getLogger();
    private static Hubbly instance;
    private FileConfiguration config;
    private FileConfiguration itemsConfig;
    private FileConfiguration serverSelectorConfig;
    private ActionManager actionManager;
    private DisabledWorlds disabledWorlds;
    private CooldownManager cooldownManager;
    private DebugMode debugMode;
    private AnnouncementsManager announcementsManager;
    private LockChat lockChat;
    private Utils utils;
    private BossBarManager bossBarManager;
    private ItemsManager itemsManager;
    private FileManager fileManager;
    private LocaleManager localeManager;
    private SubCommandManager subCommandManager;
    private HookManager hookManager;
    private ManagerFactory managerFactory;
    private StorageManager storageManager = null;
    private Services services;
    private boolean isLoaded;

    public final NamespacedKey FLY_KEY = new NamespacedKey(this, "hubbly.canfly");
    private String prefix;

    private UpdateUtil updateUtil = null;

    public void reloadPlugin() {
        debugMode.info("Restarting...");

        this.reloadConfig();
        this.saveConfig();
        config = this.getConfig();

        disabledWorlds.reload();
        subCommandManager.reload();
        fileManager.reloadFiles();
        itemsManager.reload();
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

        Services services = new Services(this);
        services.onEnable();
        this.services = services;

        disabledWorlds = new DisabledWorlds(this);
        cooldownManager = new CooldownManager();
        actionManager = new ActionManager(this);

        fileManager = new FileManager(this);
        debugMode = new DebugMode();
        itemsManager = new ItemsManager(this);
        announcementsManager = new AnnouncementsManager(this);
        lockChat = new LockChat(this);
        bossBarManager = new BossBarManager(this);
        subCommandManager = new SubCommandManager(this);
        localeManager = new LocaleManager(this);
        managerFactory = new ManagerFactory(this);

        new CommandRegistrar(this);
        new ListenerRegistrar(this);

        logger.info("Instances created");

        prefix = this.getConfig().getString("prefix");

        config = this.getConfig();
        try {
            if(config.getBoolean("anti_world_download.enabled")) {
                this.getServer().getMessenger().registerIncomingPluginChannel(this, "wdl:init", new AntiWDL(this));
                this.getServer().getMessenger().registerOutgoingPluginChannel(this, "wdl:control");
            }

            bossBarManager.reAddAllBossBars();
        } catch (Exception e) {
            e.printStackTrace();
        }

        logger.info("Components loaded");

        if (!this.isTestEnvironment()) {
            logger.info("Plugin is not in test mode");
            final int pluginId = 22219;
            new Metrics(this, pluginId);
            this.services().updateUtil().checkForUpdate(this);

            this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
            debugMode.info("BungeeCord channel registered");
        }

        logger.info("Hubbly has been enabled!");
        this.isLoaded = true;
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

        bossBarManager.removeAllBossBars();
        logger.info("Hubbly has been disabled!");
        this.getServer().getMessenger().unregisterOutgoingPluginChannel(this, "BungeeCord");
        this.getServer().getMessenger().unregisterIncomingPluginChannel(this, "wdl:init");
        this.getServer().getMessenger().unregisterOutgoingPluginChannel(this, "wdl:control");

        Bukkit.getScheduler().cancelTasks(this);
    }


    public static Hubbly getInstance() {
        return instance;
    }
    @Override
    public @NotNull FileConfiguration getConfig() {
        return super.getConfig();
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
    public DisabledWorlds getDisabledWorldsManager() {
        return disabledWorlds;
    }
    public CooldownManager getCooldownManager() {
        return cooldownManager;
    }
    public DebugMode getDebugMode() { return debugMode; }
    public AnnouncementsManager getAnnouncementsManager() { return announcementsManager; }
    public LockChat getLockChat() {return lockChat;}
    public UpdateUtil getUpdateUtil() { return updateUtil; }
    public BossBarManager getBossBarManager() { return bossBarManager; }
    public String getPrefix() {
        return prefix;
    }
    public ItemsManager getItemsManager() {return itemsManager;}
    public FileManager getFileManager() { return fileManager; }
    public LocaleManager getLocaleManager() {
        return localeManager;
    }
    public SubCommandManager getSubCommandManager() {
        return subCommandManager;
    }
    public HookManager getHookManager() {return this.hookManager;}
    public void setHookManager(HookManager hookManager) {
        this.hookManager = hookManager;
    }
    public ManagerFactory getManagerFactory() { return this.managerFactory; }
    public StorageManager getStorageManager() { return this.storageManager; }
    public static void setInstance(Hubbly hubbly) {
        instance = hubbly;
    }
    public Services services() { return this.services; }

    private boolean isTestEnvironment() {
        return Boolean.getBoolean("hubbly.test");
    }

}
