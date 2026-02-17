package me.calrl.hubbly.listeners.player;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.PluginTestBase;
import me.calrl.hubbly.enums.PluginKeys;
import me.calrl.hubbly.enums.data.PlayerVisibilityMode;
import org.bukkit.persistence.PersistentDataType;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PlayerVisibilityListenerTests extends PluginTestBase {

    @Test
    void visibilityHidden_isWrittenToPdc() {
        PlayerMock player = server.addPlayer("TestPlayer");

        plugin.services().playerVisibilityManager()
                .setHideMode(player, PlayerVisibilityMode.HIDDEN);

        String stored = player.getPersistentDataContainer().get(
                PluginKeys.PLAYER_VISIBILITY.getKey(),
                PersistentDataType.STRING
        );

        assertEquals(PlayerVisibilityMode.HIDDEN.toString(), stored);
    }
}
