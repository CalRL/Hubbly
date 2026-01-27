package me.calrl.hubbly.commands.debug;

import com.mojang.brigadier.Message;
import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.enums.Result;
import me.calrl.hubbly.handlers.PlayerMovementHandler;
import me.calrl.hubbly.utils.CommandNode;
import me.calrl.hubbly.utils.MessageBuilder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MovementNode extends CommandNode {
    private final Hubbly plugin;
    public MovementNode(Hubbly plugin) {
        super("movement");
        this.plugin = plugin;
    }

    @Override
    public Result execute(CommandSender sender, String[] args, int depth) {
        if(!(sender instanceof Player player)) return Result.PLAYER_ONLY;
        new MessageBuilder(plugin)
                .setMessage(new PlayerMovementHandler(player, plugin).getMovementMode().toString())
                .send();
        return Result.SUCCESS;
    }
}
