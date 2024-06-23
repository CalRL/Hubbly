package me.calrl.hubbly.commands;

import me.calrl.hubbly.functions.AngleRounder;
import me.calrl.hubbly.interfaces.CustomItem;
import me.calrl.hubbly.interfaces.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class SetSpawnCommand implements CommandExecutor {

    private final JavaPlugin plugin;
    private final FileConfiguration config;
    private final Map<String, CustomItem> items = new HashMap<>();

    public SetSpawnCommand(JavaPlugin plugin, FileConfiguration config) {
        this.plugin = plugin;
        this.config = config;
    }

    public double spawnRound(double value) {
        if (value == 0.5 || value == -0.5) {
            return value;
        } else {
            return Math.round(value * 2) / 2.0;
        }

    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (sender.hasPermission("hubbly.command.setspawn") || sender.isOp()) {
            Player player = (Player) sender;
            Location location = player.getServer().getPlayer(player.getName()).getLocation();
            double x = location.getX();
            double y = location.getY();
            double z = location.getZ();
            float yaw = location.getYaw();
            float pitch = location.getPitch();
            float roundedYaw = AngleRounder.roundToNearestRightAngle(yaw);
            float roundedPitch = AngleRounder.roundToNearestRightAngle(pitch);

            config.set("spawn.world", location.getWorld().getName());
            config.set("spawn.x", spawnRound(x));
            config.set("spawn.y", spawnRound(y));
            config.set("spawn.z", spawnRound(z));
            config.set("spawn.yaw", roundedYaw);
            config.set("spawn.pitch", roundedPitch);
            player.sendMessage("Set spawn successfully.");
            plugin.saveConfig();

        }
        return true;
    }
}