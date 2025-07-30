package me.calrl.hubbly;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public abstract class PluginTestBase {
    protected static ServerMock server;
    public static Hubbly plugin;

    @BeforeAll
    public static void startup() throws Exception {
        Hubbly hubbly = (Hubbly) MockBukkit.load(Hubbly.class);
        hubbly.onEnable();

        long start = System.currentTimeMillis();
        while (!hubbly.isLoaded()) {
            try {
                if (System.currentTimeMillis() - start > 5000) {
                    throw new RuntimeException("Hubbly did not finish loading in time");
                }
                Thread.sleep(10);
            } catch (Exception e) {
                throw new Exception(e.getMessage());
            }

        }
    }

    @BeforeEach
    public void init() {
        System.out.println("Starting init");
        server = MockBukkit.mock();

        server.addSimpleWorld("world_nether");
        server.addSimpleWorld("world");

        Hubbly.enableTestMode();
        plugin = MockBukkit.load(Hubbly.class);

        assertNotNull(plugin, "Plugin failed to load");
        System.out.println("Ending init");
    }

    @AfterEach
    public void cleanup() {
        MockBukkit.unmock();
    }

    @AfterAll
    public static void clean() {
        MockBukkit.unmock();
    }
}
