package me.calrl.hubbly.commands.subcommands;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.interfaces.SubCommand;
import me.calrl.hubbly.managers.AnnouncementsManager;
import me.calrl.hubbly.managers.DisabledWorlds;
import org.bukkit.entity.Player;

public class NextAnnouncementCommand implements SubCommand {
    private final Hubbly plugin;
    private AnnouncementsManager announcementsManager;
    private DisabledWorlds disabledWorlds;
    public NextAnnouncementCommand(Hubbly plugin) {
        this.plugin = plugin;
        this.announcementsManager = plugin.getAnnouncementsManager();
        this.disabledWorlds = plugin.getDisabledWorldsManager();
    }

    @Override
    public String getIdentifier() {
        return "NEXTANNOUNCEMENT";
    }

    @Override
    public void execute(Player player, String[] args) {
        if(!player.hasPermission("hubbly.command.nextannouncement") && !player.isOp()) {
            player.sendMessage(plugin.getConfig().getString("messages.no_permission_command"));
            return;
        };
        if(disabledWorlds.inDisabledWorld(player.getLocation())) return;
        announcementsManager.skipToNextAnnouncement();
    }
}
