package me.calrl.hubbly.listeners.player;

import me.calrl.hubbly.PluginTestBase;
import me.calrl.hubbly.enums.PluginKeys;
import me.calrl.hubbly.enums.data.PlayerMovementMode;
import me.calrl.hubbly.enums.data.PlayerVisibilityMode;
import me.calrl.hubbly.storage.PlayerData;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerJoinListenerTests extends PluginTestBase {

    @Test
    void playerData_valid_withMovementAndVisibility() {
        PlayerMock player = server.addPlayer();

        PersistentDataContainer container = player.getPersistentDataContainer();
        container.set(PluginKeys.MOVEMENT_KEY.getKey(),
                PersistentDataType.STRING,
                PlayerMovementMode.DOUBLEJUMP.name());

        container.set(PluginKeys.PLAYER_VISIBILITY.getKey(),
                PersistentDataType.STRING,
                PlayerVisibilityMode.HIDDEN.name());

        PlayerData data = PlayerData.from(player);

        assertTrue(data.isValid());
        assertEquals(PlayerMovementMode.DOUBLEJUMP, data.movement().getMode());
        assertEquals(PlayerVisibilityMode.HIDDEN, data.visibility().getMode());
    }

    @Test
    void playerData_valid_withMovementOnly() {
        PlayerMock player = server.addPlayer();

        player.getPersistentDataContainer().set(
                PluginKeys.MOVEMENT_KEY.getKey(),
                PersistentDataType.STRING,
                PlayerMovementMode.FLY.name()
        );

        PlayerData data = PlayerData.from(player);

        assertTrue(data.isValid());
        assertEquals(PlayerMovementMode.FLY, data.movement().getMode());
        assertEquals(PlayerVisibilityMode.VISIBLE, data.visibility().getMode());
    }

    @Test
    void playerData_valid_withNoKeys() {
        PlayerMock player = server.addPlayer();

        PlayerData data = PlayerData.from(player);

        assertTrue(data.isValid());
        assertEquals(PlayerMovementMode.NONE, data.movement().getMode());
        assertEquals(PlayerVisibilityMode.VISIBLE, data.visibility().getMode());
    }

    @Test
    void playerData_invalid_whenAllFieldsNull() {
        PlayerData data = new PlayerData(
                null,   // uuid
                null,   // name
                null,   // movement
                null    // visibility
        );

        assertFalse(data.isValid());
    }

    @Test
    void playerData_invalid_whenMovementAndVisibilityNull() {
        PlayerMock player = server.addPlayer("Bob");
        PlayerData data = new PlayerData(
                player.getUniqueId(),
                player.getName(),
                null,
                null
        );

        assertFalse(data.isValid());
    }

    @Test
    void playerData_invalid_assertValidThrows() {
        PlayerMock player = server.addPlayer("Bob");
        PlayerData data = new PlayerData(
                player.getUniqueId(),
                player.getName(),
                null,
                null
        );

        assertThrows(IllegalStateException.class, data::assertValid);
    }
}
