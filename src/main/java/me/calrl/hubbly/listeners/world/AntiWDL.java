package me.calrl.hubbly.listeners.world;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.Permissions;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

public class AntiWDL implements PluginMessageListener {
    private final Hubbly plugin;
    public AntiWDL(Hubbly plugin) {
        this.plugin = plugin;
    }
    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, @NotNull byte[] bytes) {
        FileConfiguration config = plugin.getConfig();
        if(player.hasPermission(Permissions.BYPASS_ANTI_WDL.getPermission())) {
            if(!channel.equals("wdl:init")) return;
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeInt(0);
            out.writeBoolean(false);

            player.sendPluginMessage(plugin, "wdl:control", out.toByteArray());

            for(Player p : Bukkit.getOnlinePlayers()) {
                if(p.hasPermission(Permissions.NOTIFY_WDL.getPermission())) {
                    player.sendMessage(player.getName() + " tried to download the world!");
                }
            }
            Bukkit.getLogger().info(player.getName() + " tried to download the world");
        } else {
            player.sendMessage(config.getString("messages.no_permission", "No permission."));
        }
    }
}
