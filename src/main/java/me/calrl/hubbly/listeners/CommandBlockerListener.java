package me.calrl.hubbly.listeners;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.utils.ChatUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.List;
import java.util.logging.Logger;

public class CommandBlockerListener implements Listener {

    private final Hubbly plugin;
    private List<String> blockedCommands;
    private Logger logger;
    public CommandBlockerListener(Hubbly plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
    }
    @EventHandler
    private void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        String message = event.getMessage().toLowerCase();
        FileConfiguration config = plugin.getConfig();
        blockedCommands = config.getStringList("blocked_commands");
        for (String command : blockedCommands) {
            if(message.startsWith("/" + command.toLowerCase()) & !event.getPlayer().hasPermission("hubbly.bypass")) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(ChatUtils.translateHexColorCodes(config.getString("messages.blocked_command")));
                logger.info(event.getPlayer().getName() + " tried to use /" + command);
            }
        }
    }
}
