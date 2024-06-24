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

package me.calrl.hubbly.listeners;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.functions.ParsePlaceholders;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import java.util.logging.Logger;

public class PlayerJoinListener implements Listener {

    private final Logger logger;
    private FileConfiguration config = Hubbly.getInstance().getConfig();
    public PlayerJoinListener(Logger logger) {
        this.logger = logger;
    }

    private FireworkEffect fireworkEffect() {
        FireworkEffect.Builder builder = FireworkEffect.builder()
                .withColor(Color.WHITE)
                .with(FireworkEffect.Type.valueOf(config.getString("player.join_firework.type")))
                .withTrail();

        return builder.build();
    }
    private BossBar bossBar(Player player) {
        BarColor color = BarColor.valueOf(config.getString("player.bossbar.color"));
        String text = config.getString("player.bossbar.text");
        BossBar bar = Bukkit.createBossBar(text, color, BarStyle.SOLID);
        bar.addPlayer(player);
        bar.setVisible(true);
        return bar;
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event) {
//        config = Hubbly.getInstance().getConfig();
        Player player = event.getPlayer();
        logger.info("Player " + player.getName() + " joined the game.");
        if(config.getBoolean("player.join_message.enabled")) {
            String joinMessage = config.getString("player.join_message.message");
            joinMessage = joinMessage.replace("%name%", player.getName());
            joinMessage = ParsePlaceholders.parsePlaceholders(player, joinMessage);
            event.setJoinMessage(ChatColor.translateAlternateColorCodes('&', joinMessage));
        }
        if(config.getBoolean("player.fly.enabled") && config.getBoolean("player.fly.default")) {
            event.getPlayer().setAllowFlight(true);
        }
        if(config.getBoolean("player.join_firework.enabled")) {
            try {
                Firework firework = player.getWorld().spawn(player.getLocation(), Firework.class);
                FireworkMeta meta = firework.getFireworkMeta();
                meta.addEffect(fireworkEffect());
                meta.setPower(config.getInt("player.join_firework.power"));
                firework.setFireworkMeta(meta);
            } catch (Exception e) {
                logger.warning("Failed to launch firework: " + e.getMessage());
            }
        }
        if(config.getBoolean("player.bossbar.enabled")) {
            bossBar(player);
        }
        if(config.getBoolean("player.title.enabled")) {
            String text = ChatColor.translateAlternateColorCodes('&', config.getString("player.title.text"));
            String subtitle = ChatColor.translateAlternateColorCodes('&', config.getString("player.title.subtitle"));
            int fadeIn = config.getInt("player.title.fadein");
            int stay = config.getInt("player.title.stay");
            int fadeOut = config.getInt("player.title.fadeout");
            player.sendTitle(text, subtitle, fadeIn, stay, fadeOut);
        }
        if(config.getBoolean("player.spawn_on_join")) {
            String worldName = config.getString("spawn.world");
            World world = Bukkit.getWorld(worldName);
            double x = config.getDouble("spawn.x");
            double y = config.getDouble("spawn.y");
            double z = config.getDouble("spawn.z");
            float yaw = (float) config.getDouble("spawn.yaw");
            float pitch = (float) config.getDouble("spawn.pitch");
            player.teleport(new Location(world, x, y, z, yaw, pitch));
        }
    }

    @EventHandler
    private void onPlayerLeave(PlayerQuitEvent event) {
        if(config.getBoolean("player.leave_message")) {
            Player player = event.getPlayer();
            String quitMessage = config.getString("player.leave_message.message");
            quitMessage = quitMessage.replace("%name%", player.getName());
            quitMessage = ParsePlaceholders.parsePlaceholders(player, quitMessage);
            event.setQuitMessage(ChatColor.translateAlternateColorCodes('&', quitMessage));
        }
    }
}
