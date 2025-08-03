package me.calrl.hubbly.managers;

import me.calrl.hubbly.enums.Result;
import me.calrl.hubbly.tasks.ITask;
import me.calrl.hubbly.tasks.spawn.SpawnTeleportTask;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SpawnTaskManager {
    private final Map<UUID, Location> locationMap = new HashMap<>();
    private final Map<UUID, SpawnTeleportTask> tasks = new HashMap<>();

    public Result register(Player player, Location location, SpawnTeleportTask task) {
        UUID uuid = player.getUniqueId();
        register(uuid, location, task);
        return Result.SUCCESS;
    }

    public Result register(UUID uuid, Location location, SpawnTeleportTask task) {
        locationMap.put(uuid, location);
        tasks.put(uuid, task);
        return Result.SUCCESS;
    }

    public Result unregister(UUID uuid) {
        locationMap.remove(uuid);
        tasks.remove(uuid);
        return Result.SUCCESS;
    }

    public ITask getTask(Player player) {
        UUID uuid = player.getUniqueId();
        return this.getTask(uuid);
    }

    public ITask getTask(UUID uuid) {
        return tasks.get(uuid);
    }

    public Location getStartLocation(Player player) {
        UUID uuid = player.getUniqueId();
        return this.getStartLocation(uuid);
    }

    public Location getStartLocation(UUID uuid) {
        return this.locationMap.get(uuid);
    }

    public boolean hasMoved(UUID uuid, Location newLocation) {
        Location location = this.getStartLocation(uuid);
        return !location.equals(newLocation);
    }

    public boolean isTracked(UUID uuid) {
        return this.locationMap.containsKey(uuid);
    }


}
