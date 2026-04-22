package me.calrl.hubbly.listeners.player;

import me.calrl.hubbly.PluginTestBase;
import me.calrl.hubbly.enums.Result;
import me.calrl.hubbly.managers.SpawnTaskManager;
import me.calrl.hubbly.tasks.spawn.SpawnTeleportTask;
import me.calrl.hubbly.utils.Utils;
import org.bukkit.Location;
import org.bukkit.event.player.PlayerMoveEvent;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.*;

class PlayerMoveListenerTests extends PluginTestBase {
    @Test
    void testRegisterAndUnregister() {
        PlayerMock player = server.addPlayer();
        SpawnTeleportTask task = new SpawnTeleportTask(plugin, player, 5);
        Location loc = player.getLocation();
        SpawnTaskManager registry = plugin.services().spawnTaskManager();
        assertEquals(Result.SUCCESS, registry.register(player.getUniqueId(), loc, task));
        assertEquals(Result.SUCCESS, registry.unregister(player.getUniqueId()));
    }

    @Test
    void testMovementCancelsTeleport() {
        PlayerMock player = server.addPlayer();
        SpawnTeleportTask task = new SpawnTeleportTask(plugin, player, 5);
        Location start = player.getLocation();

        task.start();

        Location newLoc = start.clone().add(20, 0, -20);
        player.simulatePlayerMove(newLoc);

        server.getScheduler().performTicks(20 * 5);
        Location spawn = Utils.getSpawn(plugin.getConfig());
        System.out.println(newLoc);
        System.out.println(player.getLocation());
        System.out.println(spawn);
        assertNotEquals(spawn, player.getLocation(), "Teleport should have been cancelled");
    }

    @Test
    void testNonMovementDoesNotCancel() {
        PlayerMock player = server.addPlayer();
        SpawnTeleportTask task = new SpawnTeleportTask(plugin, player, 2);
        Location start = player.getLocation();

        SpawnTaskManager registry = plugin.services().spawnTaskManager();

        registry.register(player.getUniqueId(), start, task);
        task.start();

        PlayerMoveEvent event = new PlayerMoveEvent(player, start, start);
        plugin.getServer().getPluginManager().callEvent(event);

        server.getScheduler().performTicks(20 * 2);

        assertEquals(new Utils(plugin).getSpawn(), player.getLocation(), "Teleport should NOT have been cancelled");
    }
}
