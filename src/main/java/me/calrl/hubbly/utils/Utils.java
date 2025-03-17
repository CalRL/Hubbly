package me.calrl.hubbly.utils;

import me.calrl.hubbly.Hubbly;
import net.minecraft.nbt.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_20_R4.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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
