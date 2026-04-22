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
        Integer slot = null;
        // slot logic
        if(args.length > 1 && args[1] != null && !args[1].isEmpty()) {
            slot = parseInt(args[1]);

            if(slot == null) {
                final String string = new MessageBuilder(plugin)
                      .setPlayer(player)
                      .setKey("arg_must_be_number")
                      .replace("%value%", args[1])
                      .build();

                plugin.getLogger().warning(string);
            }
        }
        this.dispatchGive(player, item, slot);
    }

    private void dispatchGive(Player player, String item, Integer slot) {
        String command = "hubbly give " + player.getName() + " " + item;

        if(slot != null) {
            command += " 1 " + slot;
        }

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
    }

    private Integer parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }
}
