package me.calrl.hubbly.commands.movement;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.enums.Permissions;
import me.calrl.hubbly.enums.Result;
import me.calrl.hubbly.enums.data.PlayerMovementMode;
import me.calrl.hubbly.utils.CommandNode;
import me.calrl.hubbly.utils.MessageBuilder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MovementCommand extends CommandNode {
    private final Hubbly plugin;

    public MovementCommand(Hubbly plugin) {
        super("movement");
        this.plugin = plugin;

        addChild("none", new NoneNode(plugin));
        addChild("fly", new FlyNode(plugin));
        addChild("doublejump", new DoubleJumpNode(plugin));
    }

    @Override
    public Result execute(CommandSender sender, String[] args, int depth) {
        return this.executeIfChildPresent(sender, args, depth);
    }

    protected Result executeMovement(
            CommandSender sender,
            PlayerMovementMode mode,
            Permissions permission
    ) {
        Result result = new MovementHandler(plugin, sender).execute(mode, permission);

        if (result == Result.SUCCESS && sender instanceof Player player) {
            new MessageBuilder(plugin)
                    .setPlayer(player)
                    .setKey("movement_update")
                    .replace("%movement%", mode.toString())
                    .send();
            return Result.SUCCESS;
        }

        if (result == Result.FAILURE && sender instanceof Player player) {
            plugin.getLogger().warning(
                    String.format("Failed to update movement for player: %s", player)
            );
        }

        return result;
    }
}