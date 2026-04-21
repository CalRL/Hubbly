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
import me.calrl.hubbly.utils.MessageBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ItemAction implements Action {
    @Override
    public String getIdentifier() {
        return "ITEM";
    }

    @Override
    public void execute(Hubbly plugin, Player player, String data) {
        String[] args = data.split(";");
        String item = args[0];
        if(args.length > 1 && args[1] != null) {
            try {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "hubbly give " + player.getName() + " " + item + " 1 " +  Integer.parseInt(args[1]));
            } catch(NumberFormatException e) {
                new MessageBuilder(plugin)
                        .setPlayer(player)
                        .setKey("failure")
                        .send();
                new DebugMode(plugin).info(
                        String.format("%s was passed into [ITEM] action for slotID, but it is not a number", args[1])
                );
                return;
            }


            return;
        }
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "hubbly give " + player.getName() + " " + item);
    }
}
