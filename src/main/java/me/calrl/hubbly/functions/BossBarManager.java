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
package me.calrl.hubbly.functions;


import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BossBarManager {

    private static BossBarManager instance;
    private FileConfiguration config;
    private final Map<Player, BossBar> playerBossBars = new ConcurrentHashMap<>();
    private final Map<Player, BukkitRunnable> playerAnimations = new HashMap<>();
    private final Hubbly plugin;

    public BossBarManager(Hubbly plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }

    /**
     * @deprecated 8/1/2025 bad old code, refactor to the same system every other class uses
     */
    @Deprecated(since = "2.5.4", forRemoval = true)
    public static BossBarManager getInstance() {
        return instance;
    }

    /**
     * @deprecated 8/1/2025 bad old code, refactor to the same system every other class uses
     */
    @Deprecated(since = "2.5.4", forRemoval = true)
    public static void initialize(FileConfiguration config) {
    }

    public void createBossBar(Player player) {
        if (playerBossBars.containsKey(player)) {
            return;  // Boss bar already exists for this player
        }

        BarColor color = BarColor.valueOf(config.getString("player.bossbar.color"));
        BossBar bar = Bukkit.createBossBar("", color, BarStyle.SOLID);
        bar.addPlayer(player);
        bar.setVisible(true);
        playerBossBars.put(player, bar);
        startBossBarAnimation(player, bar);
    }

    private void startBossBarAnimation(Player player, BossBar bar) {
        List<String> texts = config.getStringList("player.bossbar.animation.texts");
        if (texts.isEmpty()) {
            // Handle empty texts list
            bar.setTitle(ChatColor.RED + "No animation texts set");
            return;
        }

        int changeInterval = config.getInt("player.bossbar.animation.change-interval");

        BukkitRunnable task = new BukkitRunnable() {
            private int index = 0;

            @Override
            public void run() {
                if (!player.isOnline()) {
                    this.cancel();
                    return;
                }
                String text = ChatUtils.processMessage(player, texts.get(index));
                bar.setTitle(text);
                index = (index + 1) % texts.size();
            }
        };

        // Run task every 'changeInterval' ticks
        task.runTaskTimer(Hubbly.getInstance(), 0, changeInterval);
        playerAnimations.put(player, task);
    }

    public void removeBossBar(Player player) {
        BossBar bar = playerBossBars.remove(player);
        if (bar != null) {
            bar.removePlayer(player);
            bar.setVisible(false);  // Ensure the boss bar is hidden
        }

        BukkitRunnable task = playerAnimations.remove(player);
        if (task != null) {
            task.cancel();
        }
    }

    public void removeAllBossBars() {
        List<Player> players = new ArrayList<>(playerBossBars.keySet());
        for (Player player : players) {
            removeBossBar(player);
        }
    }

    public void reAddAllBossBars() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if(Hubbly.getInstance().getDisabledWorldsManager().inDisabledWorld(player.getWorld())) return;

            createBossBar(player);
        }
    }

    public boolean hasBossBar(Player player) {
        return player.isOnline() && playerBossBars.get(player) != null;
    }


    private void reload() {
        // TODO: make this reload in here somehow (it's 8/1/2025):
    }
}