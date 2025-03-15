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

package me.calrl.hubbly.listeners.world;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.managers.cooldown.CooldownManager;
import me.calrl.hubbly.managers.cooldown.CooldownType;
import me.calrl.hubbly.utils.ChatUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.logging.Logger;

public class LaunchpadListener implements Listener {

    private final Logger logger;
    private final Hubbly plugin;
    private FileConfiguration config;

    public LaunchpadListener(Hubbly plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if(plugin.getDisabledWorldsManager().inDisabledWorld(event.getPlayer().getLocation())) return;
        
        config = plugin.getConfig();
        if(!config.getBoolean("launchpad.enabled")) return;
        Player player = event.getPlayer();
        Block blockStandingOn = player.getLocation().getBlock(); // Get the block the player is standing on
        Block blockBelow = player.getLocation().subtract(0, 1, 0).getBlock(); // Get the block directly below the player

        Material launchpadMaterial = Material.valueOf(config.getString("launchpad.type"));
        if (blockStandingOn.getType() == launchpadMaterial || blockBelow.getType() == launchpadMaterial) {
            if (player.hasPermission("hubbly.use.launchpad") || player.isOp()) {
                if(!Hubbly.getInstance().getCooldownManager().tryCooldown(player.getUniqueId(), CooldownType.LAUNCHPAD, config.getLong("launchpad.cooldown")));
                plugin.getActionManager().executeAction(player, "[LAUNCH]");
            }

            CooldownManager cooldownManager = plugin.getCooldownManager();
            long cooldown = config.getLong("launchpad.cooldown");
            boolean cooldownResult = cooldownManager.tryCooldown(player.getUniqueId(), CooldownType.LAUNCHPAD, cooldown);

            if(!cooldownResult) return;

            plugin.getActionManager().executeAction(player, "[LAUNCH]");
        }
    }
}
