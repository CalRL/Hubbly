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
        double x = config.getDouble("spawn.x");
        double y = config.getDouble("spawn.y");
        double z = config.getDouble("spawn.z");
        float yaw = (float) config.getDouble("spawn.yaw");
        float pitch = (float) config.getDouble("spawn.pitch");

        return new Location(world, x, y, z, yaw, pitch);

    }
}
