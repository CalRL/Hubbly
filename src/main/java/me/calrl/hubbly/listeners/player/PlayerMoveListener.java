package me.calrl.hubbly.listeners.player;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.managers.DebugMode;
import me.calrl.hubbly.managers.SpawnTaskManager;
import me.calrl.hubbly.tasks.spawn.SpawnTeleportTask;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.UUID;

public class PlayerMoveListener implements Listener {
    private SpawnTaskManager registry;

    public PlayerMoveListener(Hubbly plugin) {
        this.registry = plugin.getManagerFactory().getSpawnTaskManager();
    }
    @EventHandler
    private void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        if (!registry.isTracked(uuid)) {
            return;
        }
        SpawnTeleportTask task = (SpawnTeleportTask) registry.getTask(uuid);
        Location from = task.getStartLocation();
        Location to = event.getTo();
        if(to == null) {
            return;
        }
        if (from.getX() == to.getX() && from.getZ() == to.getZ() && from.getY() == to.getY()) {
            return;
        }
        if(task.getTimer() == 0) {
            return;
        }
        new DebugMode().info("MOVED");
        task.cancel();
        registry.unregister(uuid);

    }
}
