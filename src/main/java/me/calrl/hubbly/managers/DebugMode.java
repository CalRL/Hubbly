package me.calrl.hubbly.managers;

import me.calrl.hubbly.Hubbly;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.logging.Level;
import java.util.logging.Logger;

public class DebugMode {
    private final FileConfiguration config = Hubbly.getInstance().getConfig();
    private final Logger logger = Hubbly.getInstance().getLogger();

    public void info(String message) {
        if(config.getBoolean("debug")){
            logger.log(Level.INFO, "[DEBUG] " + message);
        }
    }
    public void warn(String message) {
        if(config.getBoolean("debug")){
            logger.log(Level.WARNING, message);
        }
    }

    public void severe(String message) {
        if(config.getBoolean("debug")){
            logger.log(Level.SEVERE, message);
        }
    }

}
