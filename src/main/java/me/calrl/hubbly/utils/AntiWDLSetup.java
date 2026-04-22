package me.calrl.hubbly.utils;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.listeners.world.AntiWDL;
import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.messaging.Messenger;

public class AntiWDLSetup {

    public AntiWDLSetup(Hubbly plugin, FileConfiguration config) {
        try {
            if(config.getBoolean("anti_world_download.enabled")) {
                final Server server = plugin.getServer();
                final Messenger messenger = server.getMessenger();

                messenger.registerIncomingPluginChannel(plugin, "wdl:init", new AntiWDL(plugin));
                messenger.registerOutgoingPluginChannel(plugin, "wdl:control");
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }
}
