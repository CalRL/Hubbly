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
    protected static Hubbly plugin;

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
}
