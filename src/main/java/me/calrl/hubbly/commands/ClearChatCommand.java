package me.calrl.hubbly.commands;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.interfaces.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ClearChatCommand implements SubCommand {

    private final Hubbly plugin;
    public ClearChatCommand(Hubbly plugin) {
        this.plugin = plugin;
    }
    @Override
    public void execute(Player player, String[] args) {
        if(player.hasPermission("hubbly.clearchat") || player.isOp()) {
            int i = 0;
            while (i <= 50) {
                Bukkit.broadcastMessage("");
                i++;
            }
            plugin.getDebugMode().info("Chat cleared by " + player.getName());
        }

    }
}
