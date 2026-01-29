package me.calrl.hubbly.commands.movement;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.enums.Permissions;
import me.calrl.hubbly.enums.Result;
import me.calrl.hubbly.enums.data.PlayerMovementMode;
import me.calrl.hubbly.utils.CommandNode;
import org.bukkit.command.CommandSender;

class NoneNode extends CommandNode {
    private final Hubbly plugin;
    public NoneNode(Hubbly plugin) {
        super("none");
        this.plugin = plugin;
    }

    @Override
    public Result execute(CommandSender sender, String[] args, int depth) {
        return new MovementHandler(plugin, sender)
                .execute(PlayerMovementMode.NONE, Permissions.COMMAND_MOVEMENT_NONE);
    }
}
