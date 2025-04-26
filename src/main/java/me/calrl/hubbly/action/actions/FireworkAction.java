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
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

public class FireworkAction implements Action {
    @Override
    public String getIdentifier() {
        return "FIREWORK";
    }

    @Override
    public void execute(Hubbly plugin, Player player, String data) {
        DebugMode debugMode = new DebugMode();
        String[] args = data.split(";");
        long delay = 1;
        if(args.length == 6 && args[5] != null) {
            delay = Long.parseLong(args[5]);
        }
        Runnable runnable = () -> {
            try {
                FireworkEffect.Builder builder = FireworkEffect
                        .builder()
                        .with(FireworkEffect.Type.valueOf(args[0]))
                        .withColor(
                                Color.fromRGB(
                                        Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3])
                                )
                        )
                        .withTrail();

                Firework firework = player.getWorld().spawn(player.getLocation(), Firework.class);
                FireworkMeta meta = firework.getFireworkMeta();
                meta.addEffect(builder.build());
                meta.setPower(Integer.parseInt(args[4]));
                firework.setFireworkMeta(meta);
            } catch (Exception e) {
                debugMode.severe("Firework action failed, printing stacktrace...");
                e.printStackTrace();
            }
        };

        Bukkit.getScheduler().runTaskLater(plugin, runnable, delay);
    }
}
