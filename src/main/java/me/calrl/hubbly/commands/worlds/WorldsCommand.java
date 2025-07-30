package me.calrl.hubbly.commands.worlds;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.enums.Permissions;
import me.calrl.hubbly.enums.Result;
import me.calrl.hubbly.utils.CommandNode;
import me.calrl.hubbly.utils.MessageBuilder;
import org.bukkit.command.CommandSender;

import java.util.*;

public class WorldsCommand extends CommandNode {

    private Hubbly plugin;
    public WorldsCommand(Hubbly plugin) {
        super("worlds");
        this.plugin = plugin;

        this.loadNodes();
    }

    private void loadNodes() {
        addChild("add", new AddCommand(plugin));
        addChild("remove", new RemoveCommand(plugin));
        addChild("check", new CheckCommand(plugin));
    }

    @Override
    public Result execute(CommandSender sender, String[] args, int depth) {
        if(!sender.hasPermission(Permissions.COMMAND_WORLDS.getPermission())) {
            new MessageBuilder(plugin).setKey("no_permission_command").setPlayer(sender).send();
            return Result.NO_PERMISSION;
        }

        this.executeIfChildPresent(sender, args, depth);

        new MessageBuilder(plugin)
                .setKey("subcommands.worlds.usage")
                .setPlayer(sender)
                .send();

        return Result.USAGE_PRINTED;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args, int depth) {
        return super.tabComplete(sender, args, depth);
    }

}
