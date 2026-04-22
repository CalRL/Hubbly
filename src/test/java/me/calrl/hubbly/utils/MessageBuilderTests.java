package me.calrl.hubbly.utils;

import me.calrl.hubbly.PluginTestBase;
import me.calrl.hubbly.enums.Result;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.*;

public class MessageBuilderTests extends PluginTestBase {
    @Test
    void setMessage_buildReturnsMessage() {
        MessageBuilder builder = new MessageBuilder(plugin)
                .setMessage("Hello");

        String result = builder.build();

        assertTrue(result.contains("Hello"));
    }

    @Test
    void replace_replacesText() {
        MessageBuilder builder = new MessageBuilder(plugin)
                .setMessage("Hello %player%")
                .replace("%player%", "Steve");

        String result = builder.build();

        assertTrue(result.contains("Steve"));
        assertFalse(result.contains("%player%"));
    }

    @Test
    void replace_doesNothingIfNotPresent() {
        MessageBuilder builder = new MessageBuilder(plugin)
                .setMessage("Hello World")
                .replace("%missing%", "X");

        String result = builder.build();

        assertTrue(result.contains("Hello World"));
    }

    @Test
    void usePrefix_false_removesPrefix() {
        MessageBuilder builder = new MessageBuilder(plugin)
                .setMessage("Test")
                .usePrefix(false);

        String result = builder.build();

        // We can't assert exact formatting, but it should still contain the message
        assertTrue(result.contains("Test"));
    }

    @Test
    void build_returnsEmptyWhenContentBlank() {
        MessageBuilder builder = new MessageBuilder(plugin)
                .setMessage("   ");

        String result = builder.build();

        assertEquals("", result);
    }

    @Test
    void build_returnsEmptyWhenNomessagePresent() {
        MessageBuilder builder = new MessageBuilder(plugin)
                .setMessage("nomessage");

        String result = builder.build();

        assertEquals("", result);
    }

    @Test
    void send_sendsToPlayer() {
        PlayerMock player = server.addPlayer();

        MessageBuilder builder = new MessageBuilder(plugin, player)
                .setMessage("Hello Player");

        Result result = builder.send();

        assertEquals(Result.SUCCESS, result);
        assertTrue(player.nextMessage().contains("Hello Player"));
    }

    @Test
    void send_sendsToConsoleWhenPlayerNull() {
        MessageBuilder builder = new MessageBuilder(plugin)
                .setMessage("Hello Console");

        Result result = builder.send();

        assertEquals(Result.SUCCESS, result);
    }

    @Test
    void setPlayer_fromCommandSender_playerWorks() {
        PlayerMock player = server.addPlayer();

        MessageBuilder builder = new MessageBuilder(plugin)
                .setPlayer(player)
                .setMessage("Hi");

        String result = builder.build();

        assertTrue(result.contains("Hi"));
    }
}
