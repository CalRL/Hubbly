package me.calrl.hubbly.commands.subcommands;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.interfaces.SubCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class VersionCommand implements SubCommand {
    private final Hubbly plugin;
    private final FileConfiguration config;

    public VersionCommand(Hubbly plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }


    @Override
    public String getIdentifier() {
        return "VERSION";
    }

    @Override
    public void execute(Player player, String[] args) {
        if(player.hasPermission("hubbly.command.version") || player.isOp()) {
            player.sendMessage("Version: " + plugin.getDescription().getVersion());
            player.sendMessage("api-VERSION: " + plugin.getDescription().getAPIVersion());
            player.sendMessage("Website: " + plugin.getDescription().getWebsite());
        } else {
            player.sendMessage(config.getString("messages.no_permission_command"));
        }

    }
}
