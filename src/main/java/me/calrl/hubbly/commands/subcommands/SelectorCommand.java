package me.calrl.hubbly.commands.subcommands;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.interfaces.SubCommand;
import me.calrl.hubbly.listeners.CompassListener;
import org.bukkit.entity.Player;

public class SelectorCommand implements SubCommand {
    private final Hubbly plugin;
    public SelectorCommand(Hubbly plugin) {
        this.plugin = plugin;
    }

    public String getIdentifier() {
        return "SELECTOR";
    }
    @Override
    public void execute(Player player, String[] args) {
        if(player.hasPermission("hubbly.command.selector") || player.isOp()) {
            new CompassListener(plugin).openCompassGUI(player);
        }

    }
}
