package me.calrl.hubbly.commands.worlds;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.enums.Permissions;
import me.calrl.hubbly.enums.Result;
import me.calrl.hubbly.utils.CommandNode;
import me.calrl.hubbly.utils.MessageBuilder;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class RemoveWorldCommand extends CommandNode {
    private Hubbly plugin;
    public RemoveWorldCommand(Hubbly plugin) {
        super("remove");
        this.plugin = plugin;
    }

    @Override
    public Result execute(CommandSender sender, String[] args, int depth) {
        if(!sender.hasPermission(Permissions.COMMAND_WORLDS_REMOVE.getPermission())) {
            new MessageBuilder(plugin).setKey("no_permission_command").setPlayer(sender).send();
            return Result.NO_PERMISSION;
        }

        if(args.length <= depth) {
            new MessageBuilder(plugin).setKey("subcommands.worlds.remove.usage").setPlayer(sender).send();
            return Result.USAGE_PRINTED;
        }

        String worldName = args[depth];
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            new MessageBuilder(plugin)
                    .setKey("invalid_world")
                    .setPlayer(sender)
                    .replace("%world%", worldName)
                    .send();
            return Result.FAILURE;
        }

        plugin.getDisabledWorldsManager().removeWorld(world);

        new MessageBuilder(plugin)
                .setKey("subcommands.worlds.remove.message")
                .setPlayer(sender)
                .replace("%world%", world.getName())
                .send();
        return Result.SUCCESS;
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
