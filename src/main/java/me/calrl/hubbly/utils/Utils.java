package me.calrl.hubbly.utils;

import me.calrl.hubbly.Hubbly;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

public class Utils {

    private Hubbly plugin;
    public Utils(Hubbly plugin){
        this.plugin = plugin;
    }


    public Location getSpawn() {
        FileConfiguration config = plugin.getConfig();

        String worldName = config.getString("spawn.world");
        if(worldName == null) {
            worldName = "world";
        }

        World world = Bukkit.getWorld(worldName);
        if(world == null) {
            plugin.getDebugMode().info("UTILS: Spawn World is null");
            return null;
        }


        double x = config.getDouble("spawn.x", 0);
        double z = config.getDouble("spawn.z", 0);

        double defaultY = world.getHighestBlockAt((int) x, (int) z).getY();
        double y = config.getDouble("spawn.y", defaultY);

        float yaw = (float) config.getDouble("spawn.yaw", 0);
        float pitch = (float) config.getDouble("spawn.pitch", 0);


        return new Location(world, x, y, z, yaw, pitch);
    }
}
