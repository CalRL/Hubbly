package me.calrl.hubbly.commands.visibility;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.enums.Result;
import me.calrl.hubbly.utils.CommandNode;
import me.calrl.hubbly.utils.MessageBuilder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ShowCommand extends CommandNode {
    private final Hubbly plugin;
    public ShowCommand(Hubbly plugin) {
        super("show");
        this.plugin = plugin;
    }

    @Override
    public Result execute(CommandSender sender, String[] args, int depth) {
        if (sender instanceof Player player) {
            plugin.getManagerFactory().getPlayerVisibilityManager().setHideMode(player, false);
//            new MessageBuilder(plugin)
//                    .setPlayer(player)
//                    .setMessage("Showing players")
//                    .send();
            return Result.SUCCESS;
        }
        return Result.PLAYER_ONLY;
    }
}
