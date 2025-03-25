package me.calrl.hubbly.commands.subcommands;

import me.calrl.hubbly.enums.Permissions;
import me.calrl.hubbly.interfaces.SubCommand;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class HelpCommand implements SubCommand {
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
            return;
        }
        List<String> subCommands = new ArrayList<String>();

    }
}
