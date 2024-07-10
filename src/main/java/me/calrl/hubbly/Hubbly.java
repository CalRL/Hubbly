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
import me.calrl.hubbly.listeners.items.ConfigItemListener;
import me.calrl.hubbly.listeners.items.PlayerVisibilityListener;
import me.calrl.hubbly.listeners.player.DoubleJumpListener;
import me.calrl.hubbly.listeners.player.PlayerOffHandListener;
import me.calrl.hubbly.listeners.world.LaunchpadListener;
import me.calrl.hubbly.listeners.player.PlayerJoinListener;
import me.calrl.hubbly.listeners.player.VoidDamageListener;
import me.calrl.hubbly.listeners.world.WorldEventListeners;
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
import java.util.logging.Logger;

public final class Hubbly extends JavaPlugin {

    private final Logger logger = getLogger();
    private FileConfiguration config;
    private static Hubbly instance;
    private static final String FLY_METADATA_KEY = "hubbly.canFly";
    private FileConfiguration itemsConfig;
    private ActionManager actionManager;
    private DisabledWorlds disabledWorlds;
    private CooldownManager cooldownManager;

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
        getServer().getPluginManager().registerEvents(new LaunchpadListener(this), this);
        getServer().getPluginManager().registerEvents(new ShopListener(logger), this);
        getServer().getPluginManager().registerEvents(new ItemJoinListener(logger, this), this);
        getServer().getPluginManager().registerEvents(new SocialsListener(logger),this);
        getServer().getPluginManager().registerEvents(new VoidDamageListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(logger), this);
        getServer().getPluginManager().registerEvents(new WorldEventListeners(logger), this);
        getServer().getPluginManager().registerEvents(new ConfigItemListener(this), this);
        getServer().getPluginManager().registerEvents(new DoubleJumpListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerOffHandListener(), this);

        getCommand("hubbly").setExecutor(new HubblyCommand(logger, this));
        getCommand("setspawn").setExecutor(new SetSpawnCommand(this));
        getCommand("spawn").setExecutor(new SpawnCommand(this));
        getCommand("fly").setExecutor(new FlyCommand(logger));
    }
    @Override
    public void onEnable() {
        instance = this;
        disabledWorlds = new DisabledWorlds();
        cooldownManager = new CooldownManager();
        actionManager = new ActionManager();

        this.saveDefaultConfig();
        try{
            loadItemsFile();
            loadComponents();
            BossBarManager.initialize(Hubbly.getInstance().getConfig());
            BossBarManager.getInstance().reAddAllBossBars();

        } catch (Exception e) {
            e.printStackTrace();
        }


        logger.info("Hubbly has been enabled!");



        int pluginId = 22219;
        Metrics metrics = new Metrics(this, pluginId);

        for(Player player : Bukkit.getOnlinePlayers()) {
            player.setMetadata(FLY_METADATA_KEY, new FixedMetadataValue(Hubbly.getInstance(), false));
        }
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

    private void loadItemsFile() {
        File itemsFile = new File(getDataFolder(), "items.yml");
        if(!itemsFile.exists()) {
            saveResource("items.yml", false);
        }
        itemsConfig = YamlConfiguration.loadConfiguration(itemsFile);
    }

// todo:
//    private void registerListener(Listener listener, String isEnabledPath) {
//
//    }

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
}
