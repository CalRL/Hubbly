package me.calrl.hubbly.commands.worlds;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.enums.Result;
import me.calrl.hubbly.utils.CommandNode;
import me.calrl.hubbly.utils.MessageBuilder;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.stream.Collectors;

public class ListWorldCommand extends CommandNode {
    private Hubbly plugin;
    public ListWorldCommand(Hubbly plugin) {
        super("list");
        this.plugin = plugin;
    }

    @Override
    public Result execute(CommandSender sender, String[] args, int depth) {

        List<World> disabledWorlds = plugin.getDisabledWorldsManager().getDisabledWorlds();
        String disabledWorldNames = disabledWorlds
                .stream()
                .map(World::getName)
                .collect(Collectors.joining(", "));

        new MessageBuilder(plugin).usePrefix(true).setPlayer(sender).setMessage(disabledWorldNames).send();

        return Result.SUCCESS;
    }
}
