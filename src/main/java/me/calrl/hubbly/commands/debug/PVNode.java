package me.calrl.hubbly.commands.debug;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.enums.Permissions;
import me.calrl.hubbly.enums.PluginKeys;
import me.calrl.hubbly.enums.Result;
import me.calrl.hubbly.handlers.PlayerMovementHandler;
import me.calrl.hubbly.utils.CommandNode;
import me.calrl.hubbly.utils.MessageBuilder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

public class PVNode extends CommandNode {
    private Hubbly plugin;
    public PVNode(Hubbly plugin) {
        super("pv");
        this.plugin = plugin;
    }
    @Override
    public Result execute(CommandSender sender, String[] args, int depth) {
        if(!sender.hasPermission(Permissions.COMMAND_DEBUG.getPermission())) {
            new MessageBuilder(plugin).setKey("no_permission_command").setPlayer(sender).send();
            return Result.NO_PERMISSION;
        }

        if(!(sender instanceof Player player)) return Result.PLAYER_ONLY;
        new MessageBuilder(plugin)
                .setMessage(player.getPersistentDataContainer().get(PluginKeys.PLAYER_VISIBILITY.getKey(), PersistentDataType.STRING))
                .send();

        return null;
    }
}
