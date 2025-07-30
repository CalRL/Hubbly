package me.calrl.hubbly.commands.debug;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.enums.Permissions;
import me.calrl.hubbly.enums.Result;
import me.calrl.hubbly.managers.DebugMode;
import me.calrl.hubbly.utils.CommandNode;
import me.calrl.hubbly.utils.MessageBuilder;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class DisabledWorldsCommand extends CommandNode {

    private Hubbly plugin;
    public DisabledWorldsCommand(Hubbly plugin) {
        super("disabledworlds");
        this.plugin = plugin;
    }
    @Override
    public Result execute(CommandSender sender, String[] args, int depth) {
        MessageBuilder builder = new MessageBuilder().setPlayer(sender);
        // TODO: why is this here?
        if(!(sender instanceof Player player)) {
            builder
                .setKey("no_console")
                .send();
            return Result.PLAYER_ONLY;
        }
        if(!player.hasPermission(Permissions.COMMAND_DEBUG.get())) {
            builder.setKey("no_permission_command").send();
            return Result.NO_PERMISSION;
        }

        List<World> worlds = plugin.getDisabledWorldsManager().getDisabledWorlds();
        String worldNames = worlds.stream()
                .map(World::getName)
                .collect(Collectors.joining(", "));

        player.sendMessage("Worlds: " + worldNames);
        new DebugMode().info("Worlds: " + worldNames);
        return Result.SUCCESS;
    }
}
