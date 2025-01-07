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
package me.calrl.hubbly.managers;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class AnnouncementsManager {
    private List<String[]> announcements;
    private final Hubbly plugin;
    private final FileConfiguration config;
    private DebugMode debugMode;
    private int currentAnnouncementIndex = 0;
    private BukkitTask task;
    private AtomicBoolean state = new AtomicBoolean(true);

    public AnnouncementsManager(Hubbly plugin) {
        this.plugin = plugin;
        this.debugMode = plugin.getDebugMode();
        this.announcements = new ArrayList<>();
        this.config = plugin.getConfig();
        if(config.getBoolean("announcements.enabled")) {
            Bukkit.getScheduler().runTask(plugin, () -> {
                loadAnnouncements();
                startAnnouncementsTask();

            });
        }
    }

    public List<String[]> getAnnouncements() {
        return new ArrayList<>(announcements);
    }

    private void loadAnnouncements() {
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("announcements.messages");
        if(section == null) {
            debugMode.warn("No announcements found...");
            debugMode.warn("Disabling Announcements...");
            state.set(false);
            return;
        }

        try {
            for (String key : section.getKeys(false)) {
                List<String> messages = section.getStringList(key);
                String[] messageArray = messages.toArray(new String[0]);
                announcements.add(messageArray);
                debugMode.info(announcements.toString());
                state.set(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Start the announcements task
     * TODO: move interval into the method params
     * 7/1/2025 - why?
     */
    private void startAnnouncementsTask() {

        // Check to see if we start the task at all
        if(!state.get()) {
            debugMode.info("Disabled Announcements successfully");
            return;
        }

        int interval = plugin.getConfig().getInt("announcements.interval", 1);
        long intervalTicks = interval * 20L;

        task = Bukkit.getScheduler().runTaskTimer(plugin, this::sendNextAnnouncement, 0L, intervalTicks);

    }

    public void stopAnnouncementsTask() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }


    private void sendNextAnnouncement() {
        if (!announcements.isEmpty()) {
            String[] announcement = announcements.get(currentAnnouncementIndex);
            for (String line : announcement) {
                for(Player player : Bukkit.getOnlinePlayers()) {
                    player.sendMessage(
                            ChatUtils.centerMessage(ChatUtils.processMessage(player, line))
                    );
                }
            }
            currentAnnouncementIndex = (currentAnnouncementIndex + 1) % announcements.size();
        }
    }

    public void reloadAnnouncements() {
        stopAnnouncementsTask();
        announcements.clear();
        loadAnnouncements();
        startAnnouncementsTask();
    }

    public void skipToNextAnnouncement() {
        sendNextAnnouncement();
    }

}
