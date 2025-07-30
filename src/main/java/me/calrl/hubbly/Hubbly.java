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
import me.calrl.hubbly.commands.*;
import me.calrl.hubbly.functions.BossBarManager;
import me.calrl.hubbly.hooks.HookManager;
import me.calrl.hubbly.inventory.InventoryListener;
import me.calrl.hubbly.listeners.ServerLoadListener;
import me.calrl.hubbly.listeners.chat.ChatListener;
import me.calrl.hubbly.listeners.chat.CommandBlockerListener;
import me.calrl.hubbly.listeners.items.ConfigItemListener;
import me.calrl.hubbly.listeners.items.PlayerVisibilityListener;
import me.calrl.hubbly.listeners.player.*;
import me.calrl.hubbly.listeners.world.AntiWDL;
import me.calrl.hubbly.listeners.world.LaunchpadListener;
import me.calrl.hubbly.listeners.world.WorldEventListeners;
import me.calrl.hubbly.managers.*;
import me.calrl.hubbly.managers.cooldown.CooldownManager;
import me.calrl.hubbly.metrics.Metrics;
import me.calrl.hubbly.utils.Utils;
import me.calrl.hubbly.utils.update.UpdateUtil;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
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
    private PlayerManager playerManager;
    private BossBarManager bossBarManager;
    private ItemsManager itemsManager;
    private FileManager fileManager;
    private LocaleManager localeManager;
    private SubCommandManager subCommandManager;
    private HookManager hookManager;
    private ManagerFactory managerFactory;
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

        try {
            cleanup();
            loadComponents();
            loadFiles();
        } catch(Exception e) {
            e.printStackTrace();
        }
        debugMode.info("Restarted.");
    }

    public void loadComponents() {

        loadListeners();

        getServer().getPluginManager().registerEvents(new WorldEventListeners(this), this);
        getServer().getPluginManager().registerEvents(new ConfigItemListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerOffHandListener(this), this);
        getServer().getPluginManager().registerEvents(new WorldChangeListener(this), this);

        getCommand("hubbly").setExecutor(new HubblyCommand(this));
        getCommand("setspawn").setExecutor(new SetSpawnCommand(this));
        getCommand("spawn").setExecutor(new SpawnCommand(this));
        getCommand("fly").setExecutor(new FlyCommand(this));
        getCommand("clearchat").setExecutor(new ClearChatCommand(this));
        getCommand("lockchat").setExecutor(new LockChatCommand(this));
    }

    private void registerListener(Listener listener, String enabledPath) {
        if(config.getBoolean(enabledPath) || enabledPath.equals("null")) {
            try {
                getServer().getPluginManager().registerEvents(listener, this);
            } catch(Exception e) {
                logger.severe("PLEASE REPORT TO DEVELOPER");
                logger.severe(listener.getClass().getName()  + " failed to load, printing stacktrace...");
                debugMode.info(e.getMessage());
            }

        }
    }

    private void registerListener(Listener listener) {
        registerListener(listener, "null");
    }
    private void loadListeners() {
        registerListener(new ServerLoadListener(this));
        registerListener(new VoidDamageListener(this), "antivoid.enabled");
        registerListener(new DoubleJumpListener(this), "double_jump.enabled");
        registerListener(new PlayerVisibilityListener(), "playervisibility.enabled");
        registerListener(new LaunchpadListener(this), "launchpad.enabled");
        registerListener(new PlayerJoinListener(this));
        registerListener(new CommandBlockerListener(this));
        registerListener(new ForceinvListener(this), "player.forceinventory");
        registerListener(new ChatListener(this));
        registerListener(new InventoryListener(this));
        registerListener(new XPListener(this), "player.experience.enabled");
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

        updateUtil = new UpdateUtil();
        disabledWorlds = new DisabledWorlds(this);
        cooldownManager = new CooldownManager();
        actionManager = new ActionManager(this);

        fileManager = new FileManager(this);
        debugMode = new DebugMode();
        itemsManager = new ItemsManager(this);
        announcementsManager = new AnnouncementsManager(this);
        lockChat = new LockChat(this);
        utils = new Utils(this);
        playerManager = new PlayerManager(this);
        bossBarManager = new BossBarManager(this);
        subCommandManager = new SubCommandManager(this);
        localeManager = new LocaleManager(this);
        managerFactory = new ManagerFactory(this);



        logger.info("Instances created");

        prefix = this.getConfig().getString("prefix");

        config = this.getConfig();
        try {
            if(config.getBoolean("anti_world_download.enabled")) {
                this.getServer().getMessenger().registerIncomingPluginChannel(this, "wdl:init", new AntiWDL(this));
                this.getServer().getMessenger().registerOutgoingPluginChannel(this, "wdl:control");
            }

            loadComponents();
            bossBarManager.reAddAllBossBars();
        } catch (Exception e) {
            e.printStackTrace();
        }

        logger.info("Components loaded");

        if (!isTestMode()) {
            final int pluginId = 22219;
            new Metrics(this, pluginId);
            updateUtil.checkForUpdate(this);

            this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
            debugMode.info("BungeeCord channel registered");
        }

        logger.info("Hubbly has been enabled!");
        this.isLoaded = true;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        cleanup();

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

    /**
     * todo: put this in playermanager
     * @param player the target
     * @param state 0 (djump) or 1 (flight)
     */
    public void setPlayerFlight(Player player, int state) {
        PersistentDataContainer dataContainer = player.getPersistentDataContainer();


        if (!dataContainer.has(this.FLY_KEY, PersistentDataType.BYTE)) {
            dataContainer.set(this.FLY_KEY, PersistentDataType.BYTE, (byte) state);
        }
    }

    public boolean canPlayerFly(Player player) {
        PersistentDataContainer dataContainer = player.getPersistentDataContainer();
        Byte state = dataContainer.get(this.FLY_KEY, PersistentDataType.BYTE);
        if(state == null) {
            debugMode.info("Critical error...");
            return false;
        }
        return state == 1;
    }


    public FileConfiguration getItemsConfig() {
        return itemsConfig;
    }
    public FileConfiguration getServerSelectorConfig() { return serverSelectorConfig; }
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
    public Utils getUtils() { return utils; }
    public UpdateUtil getUpdateUtil() { return updateUtil; }
    public PlayerManager getPlayerManager() { return playerManager; }
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
    public static void setInstance(Hubbly hubbly) {
        instance = hubbly;
    }

    public static void enableTestMode() {
        testMode = true;
    }

    public static boolean isTestMode() {
        return testMode;
    }

    public boolean isLoaded() {
        return isLoaded;
    }

    public void setLoaded(boolean loaded) {
        isLoaded = loaded;
    }

}
