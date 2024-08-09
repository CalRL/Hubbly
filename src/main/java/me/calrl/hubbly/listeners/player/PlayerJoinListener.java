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

package me.calrl.hubbly.listeners.player;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.action.ActionManager;
import me.calrl.hubbly.functions.BossBarManager;
import me.calrl.hubbly.managers.DebugMode;
import me.calrl.hubbly.utils.ChatUtils;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;

public class PlayerJoinListener implements Listener {
    private final FileConfiguration config;
    private static final String FLY_METADATA_KEY = "hubbly.canFly";

    private final DebugMode debugMode;
    private final ActionManager actionManager;
    private final Hubbly plugin;

    public PlayerJoinListener(Hubbly plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        this.debugMode = plugin.getDebugMode();
        this.actionManager = plugin.getActionManager();
    }

    private FireworkEffect fireworkEffect() {
        FireworkEffect.Builder builder = FireworkEffect.builder()
                .withColor(Color.WHITE)
                .with(FireworkEffect.Type.valueOf(config.getString("player.join_firework.type")))
                .withTrail();

        return builder.build();
    }



    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if(plugin.getDisabledWorldsManager().inDisabledWorld(player.getLocation())) return;
        if(player.isOp() || plugin.needsUpdate) {
            player.sendMessage("Please update Hubbly at cal.ceo/hubbly");
        }
        plugin.setPlayerFlight(player);

        player.setAllowFlight(true);


        // Checks


//        if (config.getBoolean("player.spawn_on_join")) {
//            try {
//                String worldName = config.getString("spawn.world");
//                World world = Bukkit.getWorld(worldName);
//                double x = config.getDouble("spawn.x");
//                double y = config.getDouble("spawn.y");
//                double z = config.getDouble("spawn.z");
//                float yaw = (float) config.getDouble("spawn.yaw");
//                float pitch = (float) config.getDouble("spawn.pitch");
//                player.teleport(new Location(world, x, y, z, yaw, pitch));
//            } catch (Exception e) {
//                e.printStackTrace();
//                debugMode.warn("Couldnt teleport " + player.getName() + " to spawn");
//            }
//        }

        if (config.getBoolean("player.join_message.enabled")) {
            String joinMessage = config.getString("player.join_message.message");
            // joinMessage = ParsePlaceholders.parsePlaceholders(player, joinMessage);
            joinMessage = ChatUtils.parsePlaceholders(player, joinMessage);
            event.setJoinMessage(ChatUtils.translateHexColorCodes(joinMessage));
        }

        if (config.getBoolean("player.bossbar.enabled")) {
            BossBarManager.getInstance().createBossBar(player);
        }

        if(config.contains("actions_on_join")) {
            List<String> actions = config.getStringList("actions_on_join");
            if (!actions.isEmpty()) {
                for(String action : actions) {
                    actionManager.executeAction(Hubbly.getInstance(), player, action);
                    debugMode.info("Executed " + action);
                }

                debugMode.info(actions.toString());
            }
        }


//        if (config.getBoolean("player.title.enabled")) {
//            String text = ChatColor.translateAlternateColorCodes('&', config.getString("player.title.text"));
//            String subtitle = ChatColor.translateAlternateColorCodes('&', config.getString("player.title.subtitle"));
//            int fadeIn = config.getInt("player.title.fadein");
//            int stay = config.getInt("player.title.stay");
//            int fadeOut = config.getInt("player.title.fadeout");
//            player.sendTitle(text, subtitle, fadeIn, stay, fadeOut);
//        }


    }

    @EventHandler
    private void onPlayerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        BossBarManager.getInstance().removeBossBar(player);
        if (config.getBoolean("player.leave_message")) {
            String quitMessage = config.getString("player.leave_message.message");
            quitMessage = ChatUtils.parsePlaceholders(player, quitMessage);
            event.setQuitMessage(ChatUtils.translateHexColorCodes(quitMessage));
        }
    }
}
