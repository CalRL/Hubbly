package me.calrl.hubbly.commands.worlds;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.enums.Result;
import me.calrl.hubbly.utils.CommandNode;
import org.bukkit.command.CommandSender;

public class ListCommand extends CommandNode {
    private Hubbly plugin;
    public ListCommand(Hubbly plugin) {
        super("list");
        this.plugin = plugin;
    }

    @Override
    public Result execute(CommandSender sender, String[] args, int depth) {
        return null;
    }
}
