package me.calrl.hubbly.commands.visibility;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.enums.Permissions;
import me.calrl.hubbly.enums.Result;
import me.calrl.hubbly.utils.CommandNode;
import me.calrl.hubbly.utils.MessageBuilder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class VisibilityCommand extends CommandNode {
    private final Hubbly plugin;
    public VisibilityCommand(Hubbly plugin) {
        super("pv");
        this.plugin = plugin;
        this.loadNodes();
    }

    private void loadNodes() {
        this.addChild("show", new ShowCommand(plugin));
        this.addChild("hide", new HideCommand(plugin));
    }

    @Override
    public Result execute(CommandSender sender, String[] args, int depth) {
        if(!(sender instanceof Player player)) {
            new MessageBuilder(plugin).setKey("no_console").setPlayer(sender).send();
            return Result.PLAYER_ONLY;
        }
        if(!player.hasPermission(Permissions.COMMAND_PLAYER_VISIBILITY.getPermission())) {
            new MessageBuilder(plugin).setKey("no_permission_command").setPlayer(player).send();
            return Result.NO_PERMISSION;
        }

        this.executeIfChildPresent(sender, args, depth);

        return Result.SUCCESS;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args, int depth) {
        return super.tabComplete(sender, args, depth);
    }
}
