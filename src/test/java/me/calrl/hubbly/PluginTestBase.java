package me.calrl.hubbly;

import org.junit.jupiter.api.*;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public abstract class PluginTestBase {
    protected ServerMock server;
    protected Hubbly plugin;

    @BeforeEach
    void startup(TestInfo info) throws Exception {
        System.out.println(">>> ENTER Test " + info.getDisplayName());

        if (MockBukkit.isMocked()) {
            MockBukkit.unmock();
        }

        server = MockBukkit.mock();
        plugin = MockBukkit.load(Hubbly.class);

        plugin.getConfig().set("database.enabled", false);

        loadFile("config.yml");
        loadFile("items.yml");
        loadFile("menus/selector.yml");
        loadFile("menus/socials.yml");

        assertNotNull(plugin, "Plugin failed to load");
    }

    @AfterEach
    void cleanup(TestInfo info) {
        System.out.println(">>> FINISH Test " + info.getDisplayName());

        if (plugin != null) {
            plugin.onDisable();
        }
        MockBukkit.unmock();
    }

    private void loadFile(String resourcePath) throws IOException {
        Path target = plugin.getDataFolder().toPath().resolve(resourcePath);
        Files.createDirectories(target.getParent());

        try (InputStream in = PluginTestBase.class.getClassLoader().getResourceAsStream(resourcePath)) {
            assertNotNull(in, "Missing test resource: " + resourcePath);
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
