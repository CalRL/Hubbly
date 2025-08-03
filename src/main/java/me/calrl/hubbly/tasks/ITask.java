package me.calrl.hubbly.tasks;

import me.calrl.hubbly.enums.Result;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public interface ITask {
    Result start();
    Result cancel();
    void setTimer(long value);
    long getTimer();
    BukkitRunnable getTask();
    Player getPlayer();
}
