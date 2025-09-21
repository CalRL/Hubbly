package me.calrl.hubbly.tasks.spawn;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.enums.Result;
import me.calrl.hubbly.managers.DebugMode;
import me.calrl.hubbly.managers.SpawnTaskManager;
import me.calrl.hubbly.tasks.ITask;
import me.calrl.hubbly.utils.MessageBuilder;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class SpawnTeleportTask  implements ITask {

    private final Player player;
    private final Location startLocation;
    private final Hubbly plugin;
    private final BukkitRunnable task;
    private final Location spawn;
    private final SpawnTaskManager registry;
    private long timer;
    public SpawnTeleportTask(Hubbly plugin, Player player, long timer) {
        this.plugin = plugin;
        this.player = player;
        this.startLocation = player.getLocation();
        this.spawn = plugin.getUtils().getSpawn();
        this.registry = plugin.getManagerFactory().getSpawnTaskManager();
        this.task = new BukkitRunnable() {
            @Override
            public void run() {
                if(player.isOnline()) {
                    String soundString = plugin.getConfig().getString("spawn.sound");
                    if(soundString != null) {
                        Sound sound = Sound.valueOf(soundString);
                        player.playSound(player, sound, 1L, 1L);
                    }
                    player.teleport(spawn);
                    cleanup();
                }
            }
        };
        this.timer = timer;

    }
    public Result start() {
        registry.register(this.player.getUniqueId(), this.startLocation, this);
        this.task.runTaskLater(this.plugin, 20L * this.timer);
        
        if(this.timer >= 1) {
            new MessageBuilder(this.plugin)
                    .setPlayer(this.player)
                    .setKey("teleporting")
                    .replace("%value%", String.valueOf(this.timer))
                    .send();
        }

        return Result.SUCCESS;
    }

    public Result cancel() {
        this.task.cancel();

        this.cleanup();

        //todo: messagebuilder sends message
        new MessageBuilder(this.plugin)
                .setKey("teleport_cancelled")
                .setPlayer(player)
                .send();
        return Result.SUCCESS;
    }

    public Result cleanup() {
        this.registry.unregister(this.player.getUniqueId());
        new DebugMode().info("Cleaned up SpawnTeleportTask successfully!");
        return Result.SUCCESS;
    }

    public void setTimer(long value) {
        this.timer = value;
    }

    @Override
    public long getTimer() {
        return this.timer;
    }

    public Location getStartLocation() {
        return startLocation;
    }

    public BukkitRunnable getTask() {
        return this.task;
    }

    public Player getPlayer() {
        return this.player;
    }
}
