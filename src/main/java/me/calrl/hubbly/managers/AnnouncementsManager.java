package me.calrl.hubbly.managers;

import me.calrl.hubbly.Hubbly;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class AnnouncementsManager {
    private List<String[]> announcements;
    private final Hubbly plugin;
    private DebugMode debugMode;

    public AnnouncementsManager(Hubbly plugin) {
        this.plugin = plugin;
        this.debugMode = plugin.getDebugMode();
        run();
    }

    private void run() {
        setAnnouncements();
    }

    private List<String[]> getAnnouncements() {
        return announcements;
    }

    private void setAnnouncements() {
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("announcements.messages");
        if (section != null) {
            for (String key : section.getKeys(false)) {
                List<String> messages = section.getStringList(key);
                String[] messageArray = messages.toArray(new String[0]);
                announcements.add(messageArray);
                debugMode.info(announcements.toString());
            }
        } else {
            debugMode.warn("No announcements found...");
        }
    }

    private void clearAnnouncements() {
        announcements.clear();
    }
}
