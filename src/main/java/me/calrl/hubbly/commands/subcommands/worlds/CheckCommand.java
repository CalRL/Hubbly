package me.calrl.hubbly.commands.subcommands.worlds;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.enums.Permissions;
import me.calrl.hubbly.interfaces.SubCommand;
import me.calrl.hubbly.utils.CommandNode;
import me.calrl.hubbly.utils.MessageBuilder;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class CheckCommand extends CommandNode {
    private final Hubbly plugin;
    public CheckCommand(Hubbly plugin) {
        super("add");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args, int depth) {
        if(!sender.hasPermission(Permissions.COMMAND_WORLDS_CHECK.getPermission())) {
            new MessageBuilder(plugin).setKey("no_permission_command").setPlayer(sender).send();
            return;
        }

        if(args.length <= depth) {
            new MessageBuilder(plugin).setKey("subcommands.worlds.check.usage").setPlayer(sender).send();
            return;
        }

        String worldName = args[depth];
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            new MessageBuilder(plugin)
                    .setKey("invalid_world")
                    .setPlayer(sender)
                    .replace("%world%", worldName)
                    .send();
            return;
        }

        MessageBuilder builder = new MessageBuilder(plugin);

        boolean inDisabledWorld = plugin.getDisabledWorldsManager().inDisabledWorld(world);
        if(inDisabledWorld) {
             builder.setKey("subcommands.worlds.check.disabled")
                     .setPlayer(sender)
                     .replace("%world%", world.getName()).send();
            return;
        }

        builder.setKey("subcommands.worlds.check.enabled")
                .setPlayer(sender)
                .replace("%world%", world.getName()).send();

    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args, int depth) {
        if(args.length == depth + 1) {
            return Bukkit.getWorlds()
                    .stream()
                    .map(World::getName)
                    .filter(name -> name.startsWith(args[depth]))
                    .toList();
        }
        return Collections.emptyList();
    }

}
