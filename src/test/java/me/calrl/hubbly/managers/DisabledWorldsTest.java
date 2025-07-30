package me.calrl.hubbly.managers;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.WorldMock;
import com.sun.source.tree.AssertTree;
import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.PluginTestBase;
import org.bukkit.World;
import org.bukkit.entity.Wolf;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class DisabledWorldsTest extends PluginTestBase {

    @BeforeEach
    public void setup() {

        server.addSimpleWorld("world");
        server.addSimpleWorld("world_nether");
        server.addSimpleWorld("world_the_end");
    }

    @Test
    public void worldsExist() {
        List<World> worlds = server.getWorlds();

        World world = server.getWorld("world");
        World nether = server.getWorld("world_nether");
        World end = server.getWorld("world_the_end");

        assertTrue(worlds.contains(world));
        assertTrue(worlds.contains(nether));
        assertTrue(worlds.contains(end));
    }

    @Test
    public void addsToDisabledWorlds() {

        DisabledWorlds disabledWorlds = plugin.getDisabledWorldsManager();
        disabledWorlds.clear();

        World world = server.getWorld("world");
        disabledWorlds.addWorld(world);

        assertTrue(disabledWorlds.inDisabledWorld(world));
        assertTrue(disabledWorlds.getDisabledWorlds().contains(world));
    }

    @Test
    public void disabledWorldsTest() {
        DisabledWorlds disabledWorlds = plugin.getDisabledWorldsManager();
        disabledWorlds.clear();

        plugin.getConfig().set("disabled-worlds", List.of("world_nether", "world_the_end"));
        plugin.saveConfig();

        disabledWorlds.setDisabledWorlds();

        System.out.println(disabledWorlds.getDisabledWorldNames());

        assertTrue(disabledWorlds.getDisabledWorldNames().contains("world_nether"));
        assertTrue(disabledWorlds.getDisabledWorldNames().contains("world_the_end"));
    }

    @Test
    public void disabledWorldsInvertedTest() {
        DisabledWorlds disabledWorlds = plugin.getDisabledWorldsManager();
        disabledWorlds.clear();

        plugin.getConfig().set("disabled-worlds", List.of("world_nether", "world_the_end"));
        plugin.getConfig().set("invert", true);
        plugin.saveConfig();

        disabledWorlds.setDisabledWorlds();

        System.out.println(disabledWorlds.getDisabledWorldNames());

        assertTrue(disabledWorlds.getDisabledWorldNames().contains("world"));
    }
}
