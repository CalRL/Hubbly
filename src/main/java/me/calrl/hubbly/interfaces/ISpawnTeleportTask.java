package me.calrl.hubbly.interfaces;

import me.calrl.hubbly.enums.Result;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public interface ISpawnTeleportTask {

    Result start();
    Result cancel();
    void setTimer(long value);
    Location getStartLocation();
    BukkitRunnable getTask();
    Player getPlayer();
}
