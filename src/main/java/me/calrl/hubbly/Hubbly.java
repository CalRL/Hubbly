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
import me.calrl.hubbly.listeners.CompassListener;
import me.calrl.hubbly.listeners.ItemJoinListener;
import me.calrl.hubbly.listeners.SocialsListener;
import me.calrl.hubbly.listeners.chat.ChatListener;
import me.calrl.hubbly.listeners.chat.CommandBlockerListener;
import me.calrl.hubbly.listeners.items.ConfigItemListener;
import me.calrl.hubbly.listeners.items.MovementItemListener;
import me.calrl.hubbly.listeners.items.PlayerVisibilityListener;
import me.calrl.hubbly.listeners.player.*;
import me.calrl.hubbly.listeners.world.AntiWDL;
import me.calrl.hubbly.listeners.world.LaunchpadListener;
import me.calrl.hubbly.listeners.world.WorldEventListeners;
import me.calrl.hubbly.managers.AnnouncementsManager;
import me.calrl.hubbly.managers.LockChat;
import me.calrl.hubbly.managers.DebugMode;
import me.calrl.hubbly.managers.DisabledWorlds;
import me.calrl.hubbly.managers.cooldown.CooldownManager;
import me.calrl.hubbly.metrics.Metrics;
import me.calrl.hubbly.utils.update.UpdateChecker;
import me.calrl.hubbly.utils.update.UpdateUtil;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;

public final class Hubbly extends JavaPlugin {

    private final Logger logger = getLogger();
    private FileConfiguration config;
    private static Hubbly instance;
    private FileConfiguration itemsConfig;
    private FileConfiguration serverSelectorConfig;
    private ActionManager actionManager;
    private DisabledWorlds disabledWorlds;
    private CooldownManager cooldownManager;
    private DebugMode debugMode;
    private AnnouncementsManager announcementsManager;
    private LockChat lockChat;
    public final NamespacedKey FLY_KEY = new NamespacedKey(this, "hubbly.canfly");
    private String prefix;

    private List<Listener> listeners;
    public boolean needsUpdate;

    public void reloadPlugin() {
        this.reloadConfig();
        this.saveConfig();

        config = this.getConfig();

        try {
            debugMode.info("Restarting...");
            cleanup();
            loadComponents();
            loadFiles();

            debugMode.info("Restarted.");

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void loadComponents() {
        getServer().getPluginManager().registerEvents(new ItemJoinListener(logger, this), this);

        loadListeners();

        getServer().getPluginManager().registerEvents(new SocialsListener(logger),this);
        getServer().getPluginManager().registerEvents(new VoidDamageListener(this), this);
        getServer().getPluginManager().registerEvents(new WorldEventListeners(this), this);
        getServer().getPluginManager().registerEvents(new ConfigItemListener(this), this);
        getServer().getPluginManager().registerEvents(new DoubleJumpListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerOffHandListener(), this);

        getCommand("hubbly").setExecutor(new HubblyCommand(logger, this));
        getCommand("setspawn").setExecutor(new SetSpawnCommand(this));
        getCommand("spawn").setExecutor(new SpawnCommand(this));
        getCommand("fly").setExecutor(new FlyCommand(this));
        getCommand("clearchat").setExecutor(new ClearChatCommand(this));
        getCommand("give").setExecutor(new GiveCommand(this));
        getCommand("lockchat").setExecutor(new LockChatCommand(this));
    }

    private void registerListener(Listener listener, String enabledPath) {
        if(config.getBoolean(enabledPath) || enabledPath.equals("null")) {
            try {
                getServer().getPluginManager().registerEvents(listener, this);
            } catch(Exception e) {
                logger.severe("PLEASE REPORT TO DEVELOPER");
                logger.severe(listener  + " failed to load, printing stacktrace...");
                e.printStackTrace();
            }

        }
    }

    private void registerListener(Listener listener) {
        registerListener(listener, "null");
    }
    private void loadListeners() {
        registerListener(new CompassListener(this));
        registerListener(new PlayerVisibilityListener(), "playervisibility.enabled");
        registerListener(new LaunchpadListener(this), "launchpad.enabled");
        registerListener(new PlayerJoinListener(this));
        registerListener(new CommandBlockerListener(this));
        registerListener(new ChatListener(this), "blocked_words.enabled");
        registerListener(new MovementItemListener(this));
        registerListener(new XPListener(this), "player.experience.enabled");
    }
    @Override
    public void onEnable() {
        instance = this;
        disabledWorlds = new DisabledWorlds();
        cooldownManager = new CooldownManager();
        actionManager = new ActionManager(this);
        debugMode = new DebugMode();
        announcementsManager = new AnnouncementsManager(this);
        lockChat = new LockChat(this);
        prefix = this.getConfig().getString("prefix");

        this.saveDefaultConfig();
        config = this.getConfig();
        try {

            if(config.getBoolean("anti_world_download.enabled")) {
                this.getServer().getMessenger().registerIncomingPluginChannel(this, "wdl:init", new AntiWDL(this));
                this.getServer().getMessenger().registerOutgoingPluginChannel(this, "wdl:control");
            }
            this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
            debugMode.info("BungeeCord channel registered");
            loadFiles();
            loadComponents();
            BossBarManager.initialize(this.getConfig());
            BossBarManager.getInstance().reAddAllBossBars();

        } catch (Exception e) {
            e.printStackTrace();
        }
        final int pluginId = 22219;
        new Metrics(this, pluginId);
        new UpdateUtil().checkForUpdate(this);

        logger.info("Hubbly has been enabled!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        cleanup();

        BossBarManager.getInstance().removeAllBossBars();
        logger.info("Hubbly has been disabled!");
        this.getServer().getMessenger().unregisterOutgoingPluginChannel(this, "BungeeCord");
        this.getServer().getMessenger().unregisterIncomingPluginChannel(this, "wdl:init");
        this.getServer().getMessenger().unregisterOutgoingPluginChannel(this, "wdl:control");

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
        File serverSelectorFile = new File(getDataFolder(), "serverselector.yml");
        if(!itemsFile.exists()) {
            saveResource("items.yml", false);
        }
        itemsConfig = YamlConfiguration.loadConfiguration(itemsFile);

        if(!serverSelectorFile.exists()) {
            saveResource("serverselector.yml", false);
        }
        serverSelectorConfig = YamlConfiguration.loadConfiguration(serverSelectorFile);
    }

    private void cleanup() {
        HandlerList.unregisterAll(this);
        Bukkit.getScheduler().cancelTasks(this);
    }

    public void setPlayerFlight(Player player) {
        PersistentDataContainer dataContainer = player.getPersistentDataContainer();

        if (!dataContainer.has(this.FLY_KEY, PersistentDataType.BYTE)) {
            dataContainer.set(this.FLY_KEY, PersistentDataType.BYTE, (byte) 0);
        }
    }

    public boolean canPlayerFly(Player player) {
        PersistentDataContainer dataContainer = player.getPersistentDataContainer();
        return dataContainer.getOrDefault(this.FLY_KEY, PersistentDataType.BYTE, (byte) 0) == 1;
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
    public String getPrefix() {
        return prefix;
    }
}
