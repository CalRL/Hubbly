package com.caldev.commands;

import com.caldev.Hubbly;
import com.caldev.interfaces.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class HubblyCommand implements CommandExecutor {

    private Logger logger;
    private final FileConfiguration config;
    private final JavaPlugin plugin;
    private final Map<String, SubCommand> subCommands = new HashMap<>();

    public HubblyCommand(Logger logger, FileConfiguration config, Hubbly plugin) {
        this.logger = logger;
        this.config = config;
        this.plugin = plugin;
        registerSubCommands();
    }

    private void registerSubCommands() {
        subCommands.put("give", new GiveCommand(plugin, config));
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String String, @NotNull String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(config.getString("messages.no_console"));
            return true;
        }
        Player player = (Player) sender;

        if(args.length == 0) {
            player.sendMessage(ChatColor.YELLOW + "Usage: /hubbly <command> <args>");
            return true;
        }
        SubCommand subCommand = subCommands.get(args[0].toLowerCase());
        if(subCommand != null) {
            subCommand.execute(player, args);
        } else {
            player.sendMessage(ChatColor.YELLOW + "Unknown command.");
        }

        return true;
    }



}
