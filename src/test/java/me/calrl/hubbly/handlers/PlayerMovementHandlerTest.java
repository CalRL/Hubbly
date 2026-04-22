package me.calrl.hubbly.handlers;

import me.calrl.hubbly.PluginTestBase;
import me.calrl.hubbly.enums.PluginKeys;
import me.calrl.hubbly.enums.data.PlayerMovementMode;
import org.bukkit.Location;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerMovementHandlerTest extends PluginTestBase {
    @Test
    void handleMovement_doubleJumpCancelsFlight() {
        PlayerMock player = server.addPlayer();
        PlayerMovementHandler handler = new PlayerMovementHandler(player, plugin);

        plugin.getConfig().set("double_jump.cooldown", 1L);
        plugin.getConfig().set("double_jump.power_y", 1.0);
        plugin.getConfig().set("double_jump.power", 1.0);

        handler.setMovementMode(PlayerMovementMode.DOUBLEJUMP);

        PlayerToggleFlightEvent event =
                new PlayerToggleFlightEvent(player, true);

        handler.handleMovement(event);

        assertTrue(event.isCancelled());
        assertFalse(player.isFlying());
        assertFalse(player.getAllowFlight());
    }

    @Test
    void handleMovement_respectsCooldown() {
        PlayerMock player = server.addPlayer();
        PlayerMovementHandler handler = new PlayerMovementHandler(player, plugin);

        plugin.getConfig().set("double_jump.cooldown", 10L);

        handler.setMovementMode(PlayerMovementMode.DOUBLEJUMP);

        Vector before = player.getVelocity().clone();

        PlayerToggleFlightEvent event1 = new PlayerToggleFlightEvent(player, true);
        handler.handleMovement(event1);

        Vector afterFirst = player.getVelocity().clone();

        PlayerToggleFlightEvent event2 = new PlayerToggleFlightEvent(player, true);
        handler.handleMovement(event2);

        Vector afterSecond = player.getVelocity().clone();

        assertTrue(event1.isCancelled());
        assertTrue(event2.isCancelled());

        assertNotEquals(before, afterFirst);
        assertEquals(afterFirst, afterSecond);
    }

    @Test
    void handleJoin_defaultsToNone() {
        PlayerMock player = server.addPlayer();
        PlayerMovementHandler handler = new PlayerMovementHandler(player, plugin);

        plugin.getConfig().set("database.enabled", false);

        handler.handleJoin(null);

        String stored = player.getPersistentDataContainer()
                .get(PluginKeys.MOVEMENT_KEY.getKey(), PersistentDataType.STRING);

        assertEquals(PlayerMovementMode.NONE.getString(), stored);
        assertFalse(player.getAllowFlight());
    }

    @Test
    void handleJoin_databaseDisabled_usesPdc() {
        PlayerMock player = server.addPlayer();
        PlayerMovementHandler handler = new PlayerMovementHandler(player, plugin);

        plugin.getConfig().set("database.enabled", false);

        player.getPersistentDataContainer().set(
                PluginKeys.MOVEMENT_KEY.getKey(),
                PersistentDataType.STRING,
                PlayerMovementMode.DOUBLEJUMP.getString()
        );

        handler.handleJoin(null);

        assertTrue(player.getAllowFlight());
    }

    @Test
    void applyMode_readsFromPdc() {
        PlayerMock player = server.addPlayer();
        PlayerMovementHandler handler = new PlayerMovementHandler(player, plugin);

        player.getPersistentDataContainer().set(
                PluginKeys.MOVEMENT_KEY.getKey(),
                PersistentDataType.STRING,
                PlayerMovementMode.DOUBLEJUMP.getString()
        );

        handler.applyMode();

        assertTrue(player.getAllowFlight());
        assertFalse(player.isFlying());
    }

    @Test
    void setMovementMode_setsPdcAndFlight() {
        PlayerMock player = server.addPlayer();
        PlayerMovementHandler handler = new PlayerMovementHandler(player, plugin);

        handler.setMovementMode(PlayerMovementMode.DOUBLEJUMP);

        String stored = player.getPersistentDataContainer()
                .get(PluginKeys.MOVEMENT_KEY.getKey(), PersistentDataType.STRING);

        assertEquals(PlayerMovementMode.DOUBLEJUMP.getString(), stored);
        assertTrue(player.getAllowFlight());
        assertFalse(player.isFlying());
    }
}
