package me.calrl.hubbly.tasks.spawn;

import me.calrl.hubbly.PluginTestBase;
import me.calrl.hubbly.enums.Result;
import me.calrl.hubbly.managers.SpawnTaskManager;
import me.calrl.hubbly.utils.Utils;
import org.bukkit.Location;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.*;

public class SpawnTeleportTaskTests extends PluginTestBase {

    @Test
    void testStartReturnsSuccess() {
        PlayerMock player = server.addPlayer();
        SpawnTeleportTask task = new SpawnTeleportTask(plugin, player, 5);
        Result result = task.start();

        assertEquals(Result.SUCCESS, result);
        assertSame(player, task.getPlayer());
        assertNotNull(task.getTask());
    }

    @Test
    void testCancelUnregistersAndReturnsSuccess() {
        PlayerMock player = server.addPlayer();
        SpawnTeleportTask task = new SpawnTeleportTask(plugin, player, 5);
        task.start();

        Result result = task.cancel();

        assertEquals(Result.SUCCESS, result);

        SpawnTaskManager registry = plugin.services().spawnTaskManager();
        assertDoesNotThrow(() -> registry.unregister(player.getUniqueId()));
    }

    @Test
    void testTeleportAfterDelay() {
        PlayerMock player = server.addPlayer();
        Location start = player.getLocation();
        Location spawn = new Utils(plugin).getSpawn();
        long timer = 2;

        SpawnTeleportTask task = new SpawnTeleportTask(plugin, player, timer);
        task.start();

        server.getScheduler().performTicks(20 * timer);
        assertNotEquals(start, player.getLocation(), "Player should have been teleported to spawn");
        assertEquals(spawn, player.getLocation(), "Player should have been teleported to spawn");
    }
}
