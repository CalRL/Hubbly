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

package me.calrl.hubbly.commands;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.functions.BossBarManager;
import me.calrl.hubbly.interfaces.SubCommand;
import me.calrl.hubbly.listeners.player.PlayerJoinListener;
import me.calrl.hubbly.utils.ChatUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class ReloadCommand implements SubCommand {


    private final Logger logger;
    private FileConfiguration config = Hubbly.getInstance().getConfig();
    private final JavaPlugin plugin;
    private PlayerJoinListener playerJoinListener;

    public ReloadCommand(Logger logger, JavaPlugin plugin) {
        this.logger = logger;
        this.plugin = plugin;
    }
    @Override
    public void execute(Player player, String[] args) {
        if(player.hasPermission("hubbly.reload") || player.isOp()) {

            try {
                BossBarManager bossBarManager = BossBarManager.getInstance();
                if (bossBarManager != null) {
                    bossBarManager.removeAllBossBars();
                }
                Hubbly.getInstance().reloadPlugin();
                player.sendMessage(ChatUtils.translateHexColorCodes(config.getString("messages.reload")));
                BossBarManager.initialize(Hubbly.getInstance().getConfig());
                bossBarManager = BossBarManager.getInstance();
                bossBarManager.reAddAllBossBars();
            } catch (Exception e) {
                logger.info(String.valueOf(e));
            }

        }
    }
}
