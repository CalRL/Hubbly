package me.calrl.hubbly.commands.worlds;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.enums.Permissions;
import me.calrl.hubbly.enums.Result;
import me.calrl.hubbly.utils.CommandNode;
import me.calrl.hubbly.utils.MessageBuilder;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Map;

public class WorldsCommand extends CommandNode {

    private Hubbly plugin;
    public WorldsCommand(Hubbly plugin) {
        super("worlds");
        this.plugin = plugin;

        this.loadNodes();
    }

    private void loadNodes() {
        addChild("add", new AddWorldCommand(plugin));
        addChild("remove", new RemoveWorldCommand(plugin));
        addChild("check", new CheckWorldCommand(plugin));
        addChild("list", new ListWorldCommand(plugin));
    }

    @Override
    public Result execute(CommandSender sender, String[] args, int depth) {
        if(!sender.hasPermission(Permissions.COMMAND_WORLDS.getPermission())) {
            new MessageBuilder(plugin).setKey("no_permission_command").setPlayer(sender).send();
            return Result.NO_PERMISSION;
        }

        Result result = this.executeIfChildPresent(sender, args, depth);
        if(result != Result.NO_CHILD) {
            return result;
        }

        Map<String, CommandNode> children = this.getChildren();
        String subCommands = String.join(" | ", children.keySet());

        new MessageBuilder(plugin)
                .setKey("subcommands.worlds.usage")
                .replace("%args%", subCommands)
                .setPlayer(sender)
                .send();

        return Result.USAGE_PRINTED;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args, int depth) {
        return super.tabComplete(sender, args, depth);
    }

}
