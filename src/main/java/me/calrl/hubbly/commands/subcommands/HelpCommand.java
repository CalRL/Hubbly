package me.calrl.hubbly.commands.subcommands;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.commands.*;
import me.calrl.hubbly.enums.Permissions;
import me.calrl.hubbly.interfaces.SubCommand;
import me.calrl.hubbly.managers.LocaleManager;
import me.calrl.hubbly.utils.MessageBuilder;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HelpCommand implements SubCommand {

    private final Hubbly plugin;
    public HelpCommand(Hubbly plugin) {
        this.plugin = plugin;
    }
    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "Provides a list of commands";
    }

    @Override
    public String getUsage() {
        return "/hubbly help";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!sender.hasPermission(Permissions.COMMAND_HELP.getPermission())) {
            new MessageBuilder(plugin)
                    .setPlugin(plugin)
                    .setPlayer(sender)
                    .setKey("no_permission_command")
                    .send();
            return;
        }

        List<Command> mainCommands = new ArrayList<>();
        Command cmd = plugin.getCommand("fly");
        mainCommands.add(plugin.getCommand("hubbly"));
        mainCommands.add(plugin.getCommand("setspawn"));
        mainCommands.add(plugin.getCommand("spawn"));
        mainCommands.add(plugin.getCommand("fly"));
        mainCommands.add(plugin.getCommand("clearchat"));
        mainCommands.add(plugin.getCommand("lockchat"));

        for(Command command : mainCommands) {
            new MessageBuilder(plugin)
                    .setPlayer(sender)
                    .usePrefix(false)
                    .setKey("commands." + command.getName() + ".usage")
                    .send();
        }

        Map<String, SubCommand> subCommands = plugin.getSubCommandManager().getSubCommands();
        for(Map.Entry<String, SubCommand> entry : subCommands.entrySet()) {
            SubCommand command = entry.getValue();
            new MessageBuilder()
                    .setPlugin(plugin)
                    .setPlayer(sender)
                    .usePrefix(false)
                    .setKey("subcommands." + command.getName() + ".usage")
                    .send();
        }

    }
}
