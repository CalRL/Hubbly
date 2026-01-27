package me.calrl.hubbly.commands.movement;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.enums.Result;
import me.calrl.hubbly.utils.CommandNode;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;

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
}