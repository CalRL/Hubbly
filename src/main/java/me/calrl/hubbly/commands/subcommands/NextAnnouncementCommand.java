/*
 * This file is part of Hubbly.
 *
 * Hubbly is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Hubbly is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Hubbly. If not, see <http://www.gnu.org/licenses/>.
 */
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
