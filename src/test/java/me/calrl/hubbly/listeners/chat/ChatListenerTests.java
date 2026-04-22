package me.calrl.hubbly.listeners.chat;

import me.calrl.hubbly.PluginTestBase;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

public class ChatListenerTests extends PluginTestBase {

    private AtomicReference<AsyncPlayerChatEvent> lastChatEvent;

    @BeforeEach
    void setupChatSpy() {
        lastChatEvent = new AtomicReference<>();

        server.getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onChat(AsyncPlayerChatEvent event) {
                lastChatEvent.set(event);
            }
        }, plugin);
    }

    @Test
    void chatLockedWithBypassAllowsChat() {
        PlayerMock player = server.addPlayer();

        plugin.services().lockChat().setLocked();
        player.addAttachment(plugin, "hubbly.bypass.chatlock", true);

        player.chat("this should work");
        server.getScheduler().performTicks(1);

        assertNotNull(lastChatEvent.get());
        assertFalse(lastChatEvent.get().isCancelled());
    }

    @Test
    void chatLockedNoBypassCancelsProperly() {
        PlayerMock player = server.addPlayer();

        plugin.services().lockChat().setLocked();

        player.chat("this should be blocked");
        server.getScheduler().performTicks(1);

        assertNotNull(lastChatEvent.get());
        assertTrue(lastChatEvent.get().isCancelled());
    }

    @Test
    void chatUnlockedAllowsChat() {
        PlayerMock player = server.addPlayer();

        plugin.services().lockChat().setUnlocked();

        player.chat("normal chat");
        server.getScheduler().performTicks(1);

        assertNotNull(lastChatEvent.get());
        assertFalse(lastChatEvent.get().isCancelled());
    }

    @Test
    void blockedWordCancelMethodCancelsChat() {
        PlayerMock player = server.addPlayer();

        plugin.getConfig().set("blocked_words.enabled", true);
        plugin.getConfig().set("blocked_words.method", "CANCEL");
        plugin.getConfig().set("blocked_words.words", List.of("badword"));
        plugin.saveConfig();

        AsyncPlayerChatEvent event = new AsyncPlayerChatEvent(
                false,
                player,
                "hello badword world",
                new HashSet<>(server.getOnlinePlayers())
        );

        server.getPluginManager().callEvent(event);

        assertTrue(event.isCancelled());

    }

    @Test
    void blockedWordStarMethodReplacesWord() {
        PlayerMock player = server.addPlayer();

        plugin.getConfig().set("blocked_words.enabled", true);
        plugin.getConfig().set("blocked_words.method", "STAR");
        plugin.getConfig().set("blocked_words.words", List.of("badword"));
        plugin.saveConfig();

        player.chat("hello badword world");
        server.getScheduler().performTicks(10);
        AsyncPlayerChatEvent event = lastChatEvent.get();
        assertNotNull(event);
        assertFalse(event.isCancelled());
        assertEquals("hello ******* world", event.getMessage());
    }

    @Test
    void blockedWordIgnoredInDisabledWorld() {
        PlayerMock player = server.addPlayer();

        plugin.getConfig().set("blocked_words.enabled", true);
        plugin.getConfig().set("blocked_words.words", List.of("badword"));
        plugin.saveConfig();

        plugin.services().disabledWorlds().addWorld(player.getWorld());

        player.chat("badword");
        server.getScheduler().performTicks(1);

        AsyncPlayerChatEvent event = lastChatEvent.get();
        assertNotNull(event);
        assertFalse(event.isCancelled());
        assertEquals("badword", event.getMessage());
    }
}
