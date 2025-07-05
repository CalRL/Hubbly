package me.calrl.hubbly.commands.debug;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.enums.Permissions;
import me.calrl.hubbly.utils.CommandNode;
import me.calrl.hubbly.utils.MessageBuilder;
import org.bukkit.command.CommandSender;

import java.util.List;

public class DebugCommand extends CommandNode {
    private Hubbly plugin;
    public DebugCommand(Hubbly plugin) {
        super("debug");
        this.plugin = plugin;

        this.loadNodes();
    }

    @Override
    public void execute(CommandSender sender, String[] args, int depth) {
        if(!sender.hasPermission(Permissions.COMMAND_DEBUG.getPermission())) {
            new MessageBuilder(plugin).setKey("no_permission_command").setPlayer(sender).send();
            return;
        }

        if (args.length > depth) {
            CommandNode child = children.get(args[depth].toLowerCase());
            if (child != null) {
                child.execute(sender, args, depth + 1);
                return;
            }
        }

        new MessageBuilder(plugin)
                .setKey("subcommands.worlds.usage")
                .setPlayer(sender)
                .send();
    }

    private void loadNodes() {
        addChild("disabledworlds", new DisabledWorldsCommand(plugin));
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args, int depth) {
        return super.tabComplete(sender, args, depth);
    }
}
