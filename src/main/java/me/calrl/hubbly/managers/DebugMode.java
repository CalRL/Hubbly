package me.calrl.hubbly.managers;

import me.calrl.hubbly.Hubbly;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.logging.Level;
import java.util.logging.Logger;

public class DebugMode {
    private FileConfiguration config = Hubbly.getInstance().getConfig();
    private Logger logger = Hubbly.getInstance().getLogger();

    public void info(String message) {
        if(config.getBoolean("debug")){
            logger.log(Level.INFO, message);
        }
    }
    public void warn(String message) {
        if(config.getBoolean("debug")){
            logger.log(Level.WARNING, message);
        }
    }

}
