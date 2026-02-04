package me.calrl.hubbly.listeners.chat;

import me.calrl.hubbly.PluginTestBase;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertTrue;
public class ChatListenerTests extends PluginTestBase {
    // todo: tests
    // test 1: chat locked, player has bypass
    @Test
    void chatLockedNoBypassCancelsProperly() {
        PlayerMock player = server.addPlayer();
        plugin.getConfig().set("lock_chat", true);
        plugin.getConfig();
        plugin.getLockChat().setLocked();
        player.addAttachment(plugin, "hubbly.bypass.chatlock", true);
        assertTrue(player.hasPermission("hubbly.bypass.chatlock"));


    }
    // test 2: chat locked, player doesnt have bypass
    // test 3: chat unlocked, player doesn't have bypass

    // test 4: test bad word in disabled word
    // test 5: test bad word in enabled world

    // test 6: test bad word cancel method
    // test 7: test bad word star method
}
