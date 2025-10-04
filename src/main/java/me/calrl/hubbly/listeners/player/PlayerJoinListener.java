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
import me.calrl.hubbly.enums.Permissions;
import me.calrl.hubbly.functions.BossBarManager;
import me.calrl.hubbly.managers.DebugMode;
import me.calrl.hubbly.managers.DisabledWorlds;
import me.calrl.hubbly.utils.ChatUtils;
import me.calrl.hubbly.utils.MessageBuilder;
import me.calrl.hubbly.utils.update.UpdateUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;

public class PlayerJoinListener implements Listener {
    private FileConfiguration config;

    private final DebugMode debugMode;
    private final ActionManager actionManager;
    private final Hubbly plugin;
    private BossBarManager bossBarManager;

    public PlayerJoinListener(Hubbly plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        this.debugMode = plugin.getDebugMode();
        this.actionManager = plugin.getActionManager();
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        DisabledWorlds disabledWorlds = plugin.getDisabledWorldsManager();
        if(disabledWorlds.inDisabledWorld(player.getWorld())) return;

        this.sendUpdateMessage(player);

        boolean doubleJump = config.getBoolean("double_jump.enabled");
        if(doubleJump) {
            plugin.setPlayerFlight(player, (byte) 0);
            player.setAllowFlight(true);
        }


        if (config.getBoolean("player.join_message.enabled")) {
            String joinMessage = config.getString("player.join_message.message");
            joinMessage = ChatUtils.parsePlaceholders(player, joinMessage);
            event.setJoinMessage(ChatUtils.translateHexColorCodes(joinMessage));
        }

        if (config.getBoolean("player.bossbar.enabled")) {
            bossBarManager = plugin.getBossBarManager();
            bossBarManager.createBossBar(player);
        }

        if(config.contains("actions_on_join")) {
            this.handleActionsOnJoin(player);
        }
    }

    private void sendUpdateMessage(Player player) {
        UpdateUtil updateUtil = plugin.getUpdateUtil();
        if(updateUtil == null) {
            return;
        }

        config = plugin.getConfig();

        boolean checkForUpdate = config.getBoolean("check_for_update", true);
        if(!checkForUpdate) {
            return;
        }
        boolean sendMessage = config.getBoolean("notify_on_no_update", true);
        if(!updateUtil.getNeedsUpdate() && !sendMessage) {
            return;
        }

        if(player.hasPermission(Permissions.NOTIFY_UPDATE.getPermission())) {
            /*
            TODO: make this use locales, update updateUtil
             */
            new MessageBuilder(plugin)
                    .setPlayer(player)
                    .setKey(updateUtil.getKey())
                    .replace("%current%", updateUtil.getCurrent())
                    .replace("%new%", updateUtil.getNew())
                    .send();
        }
    }

    private void handleActionsOnJoin(Player player) {
        FileConfiguration config = plugin.getConfig();
        List<String> actions = config.getStringList("actions_on_join");
        if(actions.isEmpty()) {
            return;
        }

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            for(String action : actions) {
                actionManager.executeAction(player, action, true);
                debugMode.info("Executed " + action);
            }

            debugMode.info(actions.toString());
        }, 1L);
    }

    @EventHandler
    private void onPlayerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        bossBarManager = plugin.getBossBarManager();
        bossBarManager.removeBossBar(player);
        boolean isEnabled = config.getBoolean("player.leave_message.enabled");
        if(isEnabled) {
            String quitMessage = config.getString("player.leave_message.message");
            quitMessage = ChatUtils.parsePlaceholders(player, quitMessage);
            event.setQuitMessage(ChatUtils.translateHexColorCodes(quitMessage));
        }
    }
}
