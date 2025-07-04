package me.calrl.hubbly.commands;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import me.calrl.hubbly.PluginTestBase;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class SpawnCommandTest extends PluginTestBase {

    private static SpawnCommand spawnCommand;
    private static Command mockCommand;

    @BeforeEach
    void setUp() {
        World world = server.addSimpleWorld("world");
        assertNotNull(Bukkit.getWorld(world.getName()));

        spawnCommand = new SpawnCommand(plugin);
        mockCommand = new Command("spawn") {
            @Override
            public boolean execute(CommandSender sender, String label, String[] args) {
                return true;
            }
        };

        // Setup spawn location in config
        FileConfiguration config = plugin.getConfig();
        config.set("spawn.world", "world");
        config.set("spawn.x", 100);
        config.set("spawn.y", 64);
        config.set("spawn.z", 100);
        config.set("spawn.yaw", 0);
        config.set("spawn.pitch", 0);
    }

    @Test
    void testConsoleCannotUseSpawnCommand() {
        CommandSender console = server.getConsoleSender();
        boolean result = spawnCommand.onCommand(console, mockCommand, "spawn", new String[0]);
        assertTrue(result); // Should still return true
    }

    @Test
    void testPlayerIsTeleportedToSpawn() {
        PlayerMock player = server.addPlayer("TestPlayer");
        player.setOp(true);

        boolean result = spawnCommand.onCommand(player, mockCommand, "spawn", new String[0]);
        assertTrue(result);

        server.getScheduler().performTicks(200);

        FileConfiguration config = plugin.getConfig();
        config.get("spawn.x");
        config.get("spawn.z");

        Location loc = player.getLocation();
        System.out.println("Player location: " + loc);
        assertEquals(100, loc.getX(), 0.5);
        assertEquals(64, loc.getY(), 0.5);
        assertEquals(100, loc.getZ(), 0.5);
    }
}
