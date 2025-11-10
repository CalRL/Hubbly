package me.calrl.hubbly.commands.visibility;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.enums.Result;
import me.calrl.hubbly.utils.CommandNode;
import me.calrl.hubbly.utils.MessageBuilder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HideCommand extends CommandNode {
    private final Hubbly plugin;
    public HideCommand(Hubbly plugin) {
        super("hide");
        this.plugin = plugin;
    }

    @Override
    public Result execute(CommandSender sender, String[] args, int depth) {
        if (sender instanceof Player player) {
            plugin.getManagerFactory().getPlayerVisibilityManager().setHideMode(player, true);
//            new MessageBuilder(plugin)
//                    .setPlayer(player)
//                    .setMessage("Hiding players")
//                    .send();
            return Result.SUCCESS;
        }
        return Result.PLAYER_ONLY;
    }
}
