package me.calrl.hubbly;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public abstract class PluginTestBase {
    protected static ServerMock server;
    public static Hubbly plugin;

    @BeforeAll
    public static void startup() throws Exception {

        if (!MockBukkit.isMocked()) {
            server = MockBukkit.mock();
        } else {
            server = MockBukkit.getMock();
        }

        plugin = (Hubbly) MockBukkit.load(Hubbly.class);
        plugin.onEnable();

        long start = System.currentTimeMillis();
        while (!plugin.isLoaded()) {
            try {
                if (System.currentTimeMillis() - start > 5000) {
                    throw new RuntimeException("Hubbly did not finish loading in time");
                }
                Thread.sleep(10);
            } catch (Exception e) {
                throw new Exception(e.getMessage());
            }
        }

        loadFile("config.yml");
        loadFile("items.yml");
        loadFile("menus/selector.yml");
        loadFile("menus/socials.yml");
    }

    @BeforeEach
    public void init() {

        Hubbly.enableTestMode();

        assertNotNull(plugin, "Plugin failed to load");
    }

//    @AfterEach
//    public void cleanup() {
//        MockBukkit.unmock();
//    }
//
    @AfterAll
    public static void clean() {
        MockBukkit.unmock();
        System.out.println("cleanup!");
    }

    private static void loadFile(String resourcePath) throws IOException {
        Path target = plugin.getDataFolder().toPath().resolve(resourcePath);
        Files.createDirectories(target.getParent());

        try (InputStream in = PluginTestBase.class.getClassLoader().getResourceAsStream(resourcePath)) {
            assertNotNull(in, "Missing test resource: " + resourcePath);
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
