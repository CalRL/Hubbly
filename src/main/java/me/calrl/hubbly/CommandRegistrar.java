package me.calrl.hubbly;

import me.calrl.hubbly.commands.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;

public class CommandRegistrar {
    private final Hubbly plugin;
    public CommandRegistrar(Hubbly plugin) {
        this.plugin = plugin;
        this.start();
    }

    private void start() {
        this.registerCommand("hubbly", new HubblyCommand(plugin));
        this.registerCommand("setspawn", new SetSpawnCommand(plugin), "spawn.enabled");
        this.registerCommand("spawn", new SpawnCommand(plugin), "spawn.enabled");
        this.registerCommand("fly", new FlyCommand(plugin));
        this.registerCommand("clearchat", new ClearChatCommand(plugin));
        this.registerCommand("lockchat", new LockChatCommand(plugin));
    }

    private void registerCommand(String commandName, CommandExecutor commandClass) {
        PluginCommand command = plugin.getCommand(commandName);

        if (command == null) {
            plugin.getLogger().severe(
                    String.format("Command '%s' does not exist in plugin.yml", commandName)
            );
            return;
        }

        command.setExecutor(commandClass);
    }

    private void registerCommand(String commandName, CommandExecutor commandClass, String enabledPath) {
        PluginCommand command = plugin.getCommand(commandName);

        boolean enabled = this.plugin.getConfig().getBoolean(enabledPath);
        if(!enabled) return;
        if (command == null) {
            plugin.getLogger().severe(
                    String.format("Command '%s' does not exist in plugin.yml", commandName)
            );
            return;
        }

        command.setExecutor(commandClass);
    }
}
