package me.calrl.hubbly.commands.debug;

import com.mojang.brigadier.Message;
import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.enums.Permissions;
import me.calrl.hubbly.managers.DebugMode;
import me.calrl.hubbly.utils.CommandNode;
import me.calrl.hubbly.utils.MessageBuilder;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class DisabledWorldsCommand extends CommandNode {

    private Hubbly plugin;
    public DisabledWorldsCommand(Hubbly plugin) {
        super("disabledworlds");
        this.plugin = plugin;
    }
    @Override
    public void execute(CommandSender sender, String[] args, int depth) {
        MessageBuilder builder = new MessageBuilder().setPlayer(sender);
        if(!(sender instanceof Player player)) {
            builder
                .setKey("no_console")
                .send();
            return;
        }
        if(!player.hasPermission(Permissions.COMMAND_DEBUG.get())) {
            builder.setKey("no_permission_command").send();
            return;
        }

        List<World> worlds = plugin.getDisabledWorldsManager().getDisabledWorlds();
        String worldNames = worlds.stream()
                .map(World::getName)
                .collect(Collectors.joining(", "));

        player.sendMessage("Worlds: " + worldNames);
        new DebugMode().info("Worlds: " + worldNames);
    }
}
