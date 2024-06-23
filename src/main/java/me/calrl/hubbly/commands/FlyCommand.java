package me.calrl.hubbly.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.logging.Logger;

public class FlyCommand implements CommandExecutor {

    private final Logger logger;
    private final FileConfiguration config;
    public FlyCommand(Logger logger, FileConfiguration config) {
        this.logger = logger;
        this.config = config;
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(sender instanceof Player && config.getBoolean("player.fly.enabled")) {
            if(sender.hasPermission("hubbly.command.fly") || sender.isOp()) {
                Player player = (Player) sender;
                if (player.getAllowFlight()) {
                    player.setAllowFlight(false);
                    player.setFlying(false);
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString("messages.fly.disable"))));
                } else {
                    player.setAllowFlight(true);
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString("messages.fly.enable"))));
                }
            }

        }

        return true;
    }
}
