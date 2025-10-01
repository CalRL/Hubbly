package me.calrl.hubbly.commands.debug;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.enums.Permissions;
import me.calrl.hubbly.enums.Result;
import me.calrl.hubbly.managers.ItemsManager;
import me.calrl.hubbly.utils.CommandNode;
import org.bukkit.command.CommandSender;

import java.util.Set;
import java.util.stream.Collectors;

public class ItemsCommand extends CommandNode {
    private final Hubbly plugin;
    public ItemsCommand(Hubbly plugin) {
        super("items");
        this.plugin = plugin;
    }

    @Override
    public Result execute(CommandSender sender, String[] args, int depth) {
        ItemsManager manager = plugin.getItemsManager();
        if(!sender.hasPermission(Permissions.COMMAND_DEBUG.getPermission())) {
            return Result.NO_PERMISSION;
        }
        Set<String> set = manager.getItems().keySet();
        String message = String.join(",", set);
        sender.sendMessage(message);
        return Result.SUCCESS;
    }
}
