package me.calrl.hubbly.commands.movement;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.enums.Result;
import me.calrl.hubbly.utils.CommandNode;
import org.bukkit.command.CommandSender;

public class NoneNode extends CommandNode {
    private final Hubbly plugin;
    public NoneNode(Hubbly plugin) {
        super("none");
        this.plugin = plugin;
    }
    @Override
    public Result execute(CommandSender sender, String[] args, int depth) {
        return null;
    }
}
