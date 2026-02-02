package me.calrl.hubbly.commands;

import me.calrl.hubbly.PluginTestBase;
import me.calrl.hubbly.enums.Permissions;
import me.calrl.hubbly.enums.PluginKeys;
import me.calrl.hubbly.enums.Result;
import me.calrl.hubbly.enums.data.PlayerMovementMode;
import org.bukkit.persistence.PersistentDataType;
import org.junit.jupiter.api.Test;
import me.calrl.hubbly.commands.movement.MovementHandler;
import org.mockbukkit.mockbukkit.command.ConsoleCommandSenderMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MovementCommandTest extends PluginTestBase {
    @Test
    void consoleSender_returnsPlayerOnly() {
        ConsoleCommandSenderMock console = server.getConsoleSender();

        Result result = new MovementHandler(plugin, console)
                .execute(PlayerMovementMode.FLY, Permissions.COMMAND_MOVEMENT_FLY);

        assertEquals(Result.PLAYER_ONLY, result);
    }

    @Test
    void playerWithoutPermission_returnsNoPermission() {
        PlayerMock player = server.addPlayer();

        Result result = new MovementHandler(plugin, player)
                .execute(PlayerMovementMode.FLY, Permissions.COMMAND_MOVEMENT_FLY);

        assertEquals(Result.NO_PERMISSION, result);
    }

    @Test
    void playerWithPermission_returnsSuccess() {
        PlayerMock player = server.addPlayer();
        player.addAttachment(plugin, Permissions.COMMAND_MOVEMENT_FLY.getPermission(), true);

        Result result = new MovementHandler(plugin, player)
                .execute(PlayerMovementMode.FLY, Permissions.COMMAND_MOVEMENT_FLY);

        assertEquals(Result.SUCCESS, result);
    }

    @Test
    void movementModeIsAppliedToPlayer() {
        PlayerMock player = server.addPlayer();
        player.addAttachment(plugin, Permissions.COMMAND_MOVEMENT_DOUBLEJUMP.getPermission(), true);

        Result result = new MovementHandler(plugin, player)
                .execute(PlayerMovementMode.DOUBLEJUMP, Permissions.COMMAND_MOVEMENT_DOUBLEJUMP);

        assertEquals(Result.SUCCESS, result);

        PlayerMovementMode stored =
                PlayerMovementMode.valueOf(
                        player.getPersistentDataContainer()
                                .get(PluginKeys.MOVEMENT_KEY.getKey(), PersistentDataType.STRING)
                );

        assertEquals(PlayerMovementMode.DOUBLEJUMP, stored);
    }

    @Test
    void noneModeClearsMovement() {
        PlayerMock player = server.addPlayer();
        player.addAttachment(plugin, Permissions.COMMAND_MOVEMENT_NONE.getPermission(), true);

        Result result = new MovementHandler(plugin, player)
                .execute(PlayerMovementMode.NONE, Permissions.COMMAND_MOVEMENT_NONE);

        assertEquals(Result.SUCCESS, result);
    }
}
