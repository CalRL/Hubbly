package me.calrl.hubbly.commands;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.PluginTestBase;
import me.calrl.hubbly.enums.data.PlayerMovementMode;
import me.calrl.hubbly.handlers.PlayerMovementHandler;
import me.calrl.hubbly.managers.LocaleManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.persistence.PersistentDataType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.*;

public class FlyCommandTest extends PluginTestBase {

    private FlyCommand flyCommand;

    @BeforeEach
    public void setupFlyCommand() {
        flyCommand = new FlyCommand(plugin);
        plugin.getConfig().set("player.fly.enabled", true);
    }

    private Command mockCommand() {
        return new Command("fly") {
            @Override
            public boolean execute(CommandSender sender, String commandLabel, String[] args) {
                return true;
            }
        };
    }

    @Test
    public void testConsoleCannotUseFlyCommand() {
        CommandSender console = server.getConsoleSender();
        boolean result = flyCommand.onCommand(console, mockCommand(), "fly", new String[0]);

        assertTrue(result);
    }

    @Test
    public void testPlayerWithoutPermissionIsBlocked() {
        PlayerMock player = server.addPlayer("NoPerms");
        player.setOp(false);

        flyCommand.onCommand(player, mockCommand(), "fly", new String[0]);
        String message = player.nextMessage();

        assertNotNull(message);
        assertTrue(message.toLowerCase().contains("permission"));
    }

    @Test
    public void testFlyTogglesCorrectly() {
        PlayerMock player = server.addPlayer("FlyGuy");
        player.setOp(true);

        flyCommand.onCommand(player, mockCommand(), "fly", new String[0]);
        PlayerMovementHandler handler = new PlayerMovementHandler(player, plugin);
        PlayerMovementMode mode = handler.getMovementMode();
        assertEquals(PlayerMovementMode.FLY, mode);

        flyCommand.onCommand(player, mockCommand(), "fly", new String[0]);
        PlayerMovementMode newMode = handler.getMovementMode();
        assertEquals(PlayerMovementMode.NONE, newMode);
    }

    @Test
    public void testHubbly() {
        Hubbly plugin = Hubbly.getInstance();

        LocaleManager manager = plugin.getLocaleManager();

        String lang = manager.getDefaultLanguage();

        assertEquals("en", lang);
    }
}
