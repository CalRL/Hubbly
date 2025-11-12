package me.calrl.hubbly.commands;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import me.calrl.hubbly.PluginTestBase;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HubblyCommandTest extends PluginTestBase {

    private static HubblyCommand hubblyCommand;

    @BeforeEach
    public void setupCommand() {
        hubblyCommand = new HubblyCommand(plugin);
    }

    private Command mockCommand() {
        return new Command("hubbly") {
            @Override
            public boolean execute(CommandSender sender, String commandLabel, String[] args) {
                return true;
            }
        };
    }

    @Test
    public void testNoArgsShowsUsageMessage() {
        PlayerMock player = server.addPlayer("TestUser");

        boolean result = hubblyCommand.onCommand(player, mockCommand(), "hubbly", new String[0]);
        assertTrue(result);

        String msg = player.nextMessage();
        System.out.println("[Test] /hubbly (no args) -> " + msg);
        assertNotNull(msg);
        assertTrue(msg.toLowerCase().contains("usage"));
    }

    @Test
    public void testUnknownSubcommandShowsError() {
        PlayerMock player = server.addPlayer("TestUser");

        boolean result = hubblyCommand.onCommand(player, mockCommand(), "hubbly", new String[]{"wat"});
        assertTrue(result);

        String msg = player.nextMessage();
        System.out.println("[Test] /hubbly wat -> " + msg);
        assertNotNull(msg);
        assertTrue(msg.toLowerCase().contains("unknown"));
    }
}
