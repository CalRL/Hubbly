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
import me.calrl.hubbly.listeners.*;
import me.calrl.hubbly.listeners.chat.ChatListener;
import me.calrl.hubbly.listeners.chat.CommandBlockerListener;
import me.calrl.hubbly.listeners.items.ConfigItemListener;
import me.calrl.hubbly.listeners.items.PlayerVisibilityListener;
import me.calrl.hubbly.listeners.player.DoubleJumpListener;
import me.calrl.hubbly.listeners.player.PlayerOffHandListener;
import me.calrl.hubbly.listeners.world.LaunchpadListener;
import me.calrl.hubbly.listeners.player.PlayerJoinListener;
import me.calrl.hubbly.listeners.player.VoidDamageListener;
import me.calrl.hubbly.listeners.world.WorldEventListeners;
import me.calrl.hubbly.managers.DebugMode;
import me.calrl.hubbly.managers.DisabledWorlds;
import me.calrl.hubbly.managers.cooldown.CooldownManager;
import me.calrl.hubbly.metrics.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;

public final class Hubbly extends JavaPlugin {

    private final Logger logger = getLogger();
    private FileConfiguration config;
    private static Hubbly instance;
    private static final String FLY_METADATA_KEY = "hubbly.canFly";
    private FileConfiguration itemsConfig;
    private FileConfiguration serverSelectorConfig;
    private ActionManager actionManager;
    private DisabledWorlds disabledWorlds;
    private CooldownManager cooldownManager;
    private DebugMode debugMode;

    private List<Listener> listeners;

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
        getServer().getPluginManager().registerEvents(new ItemJoinListener(logger, this), this);
        loadListeners();

        getServer().getPluginManager().registerEvents(new SocialsListener(logger),this);
        getServer().getPluginManager().registerEvents(new VoidDamageListener(this), this);
        getServer().getPluginManager().registerEvents(new WorldEventListeners(this), this);
        getServer().getPluginManager().registerEvents(new ConfigItemListener(this), this);
        getServer().getPluginManager().registerEvents(new DoubleJumpListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerOffHandListener(), this);

        getCommand("hubbly").setExecutor(new HubblyCommand(logger, this));
        getCommand("setspawn").setExecutor(new SetSpawnCommand(this));
        getCommand("spawn").setExecutor(new SpawnCommand(this));
        getCommand("fly").setExecutor(new FlyCommand(logger));
        getCommand("clearchat").setExecutor(new ClearChatCommand(this));
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
        registerListener(new ChatListener(this));
    }
    @Override
    public void onEnable() {
        instance = this;
        disabledWorlds = new DisabledWorlds();
        cooldownManager = new CooldownManager();
        actionManager = new ActionManager();
        debugMode = new DebugMode();

        this.saveDefaultConfig();
        config = this.getConfig();
        try {

            loadFiles();
            loadComponents();
            BossBarManager.initialize(Hubbly.getInstance().getConfig());
            BossBarManager.getInstance().reAddAllBossBars();

        } catch (Exception e) {
            e.printStackTrace();
        }

        int pluginId = 22219;
        Metrics metrics = new Metrics(this, pluginId);

        for(Player player : Bukkit.getOnlinePlayers()) {
            player.setMetadata(FLY_METADATA_KEY, new FixedMetadataValue(Hubbly.getInstance(), false));
        }

        logger.info("Hubbly has been enabled!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        BossBarManager.getInstance().removeAllBossBars();
        logger.info("Hubbly has been disabled!");
    }


    public static Hubbly getInstance() {
        return getPlugin(Hubbly.class);
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



// todo:
//    private void registerListener(Listener listener, String isEnabledPath) {
//
//    }

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
}
