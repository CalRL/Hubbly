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
package me.calrl.hubbly.action.actions;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.action.Action;
import me.calrl.hubbly.managers.DebugMode;
import me.calrl.hubbly.utils.ChatUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TitleAction implements Action {
    @Override
    public String getIdentifier() {
        return "TITLE";
    }
    private DebugMode debugMode;

    @Override
    public void execute(Hubbly plugin, Player player, String data) {
        debugMode = plugin.getDebugMode();
        String title;
        String subtitle;
        int fadeIn;
        int stay;
        int fadeOut;

        String[] args = data.split(";");
        if(args.length != 5) {
            debugMode.warn("Invalid data format, expected 4 args, got " + args.length);
        }

        title = ChatUtils.processMessage(player, args[0]);

        subtitle = args[1].isEmpty() ? "" : ChatUtils.processMessage(player, args[1]);

        try {
            fadeIn = Integer.parseInt(args[2]);
            stay = Integer.parseInt(args[3]);
            fadeOut = Integer.parseInt(args[4]);
            player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);

        } catch(NumberFormatException e) {
            debugMode.warn("Invalid data format, sending defaults");
            title = "Hubbly";
            subtitle = "";
            fadeIn = 20;
            stay = 200;
            fadeOut = 20;
            player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
        }

    }
}
