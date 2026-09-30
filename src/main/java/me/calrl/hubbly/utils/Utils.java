package me.calrl.hubbly.utils;

import me.calrl.hubbly.Hubbly;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

import javax.annotation.Nullable;
import java.net.URI;
import java.util.Optional;
import java.util.regex.Pattern;

public class Utils {

    private Hubbly plugin;
    private static final Pattern URL_PATTERN = Pattern.compile(
            "^https?://(?:www\\.)?[-a-zA-Z0-9@:%._+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b(?:[-a-zA-Z0-9()@:%_+.~#?&/=]*)$"
    );
    public Utils(Hubbly plugin){
        this.plugin = plugin;
    }

    public Location getSpawn() {
        FileConfiguration config = plugin.getConfig();

        String worldName = config.getString("spawn.world", "world");

        World world = Bukkit.getWorld(worldName);
        double x = config.getDouble("spawn.x");
        double y = config.getDouble("spawn.y");
        double z = config.getDouble("spawn.z");
        float yaw = (float) config.getDouble("spawn.yaw");
        float pitch = (float) config.getDouble("spawn.pitch");

        return new Location(world, x, y, z, yaw, pitch);
    }

    public static Optional<Location> getSpawn(FileConfiguration config) {
        String worldName = config.getString("spawn.world", "world");

        World world = Bukkit.getWorld(worldName);
        if(world == null) {
            return Optional.empty();
        }
        double x = config.getDouble("spawn.x");
        double y = config.getDouble("spawn.y");
        double z = config.getDouble("spawn.z");
        float yaw = (float) config.getDouble("spawn.yaw");
        float pitch = (float) config.getDouble("spawn.pitch");

        return Optional.of(new Location(world, x, y, z, yaw, pitch));
    }

    public static String normalizeUrl(String url) {
        String trimmed = url.trim();

        if (!trimmed.startsWith("http://") && !trimmed.startsWith("https://")) {
            return "https://" + trimmed;
        }

        return trimmed;
    }

    public static Boolean isValidUrl(@Nullable String url) {
        if (url == null) {
            return false;
        }
        try {
            URI uri = URI.create(url);
            return ("http".equalsIgnoreCase(uri.getScheme()) || "https".equalsIgnoreCase(uri.getScheme()) && uri.getHost() != null);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
