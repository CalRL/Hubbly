package me.calrl.hubbly.utils;

import me.calrl.hubbly.PluginTestBase;
import me.calrl.hubbly.enums.Result;
import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.command.ConsoleCommandSenderMock;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CommandNodeTests extends PluginTestBase {

    static class TestNode extends CommandNode {
        private final Result result;

        TestNode(String identifier, Result result) {
            super(identifier);
            this.result = result;
        }

        @Override
        public Result execute(CommandSender sender, String[] args, int depth) {
            return result;
        }
    }

    private CommandSender sender() {
        return new ConsoleCommandSenderMock();
    }

    @Test
    void matches_isCaseInsensitive() {
        CommandNode node = new TestNode("test", Result.SUCCESS);

        assertTrue(node.matches("test"));
        assertTrue(node.matches("TEST"));
        assertTrue(node.matches("TeSt"));
        assertFalse(node.matches("other"));
    }

    @Test
    void addChild_registersLowercaseKey() {
        CommandNode root = new TestNode("root", Result.SUCCESS);
        CommandNode child = new TestNode("child", Result.SUCCESS);

        root.addChild("Child", child);

        assertTrue(root.getChildren().containsKey("child"));
    }

    @Test
    void executeIfChildPresent_executesChild() {
        CommandNode root = new TestNode("root", Result.NO_CHILD);
        CommandNode child = new TestNode("child", Result.SUCCESS);

        root.addChild("child", child);

        Result result = root.executeIfChildPresent(
                sender(),
                new String[]{"child"},
                0
        );

        assertEquals(Result.SUCCESS, result);
    }

    @Test
    void executeIfChildPresent_returnsNoChild_whenMissing() {
        CommandNode root = new TestNode("root", Result.SUCCESS);

        Result result = root.executeIfChildPresent(
                sender(),
                new String[]{"missing"},
                0
        );

        assertEquals(Result.NO_CHILD, result);
    }

    @Test
    void executeIfChildPresent_returnsNoChild_whenArgsTooShort() {
        CommandNode root = new TestNode("root", Result.SUCCESS);

        Result result = root.executeIfChildPresent(
                sender(),
                new String[]{},
                0
        );

        assertEquals(Result.NO_CHILD, result);
    }

    @Test
    void tabComplete_returnsMatchingChildren() {
        CommandNode root = new TestNode("root", Result.SUCCESS);
        root.addChild("add", new TestNode("add", Result.SUCCESS));
        root.addChild("remove", new TestNode("remove", Result.SUCCESS));
        root.addChild("reload", new TestNode("reload", Result.SUCCESS));

        List<String> results = root.tabComplete(
                sender(),
                new String[]{"re"},
                0
        );

        assertEquals(2, results.size());
        assertTrue(results.contains("remove"));
        assertTrue(results.contains("reload"));
    }

    @Test
    void tabComplete_delegatesToChild() {
        CommandNode root = new TestNode("root", Result.SUCCESS);
        CommandNode sub = new TestNode("sub", Result.SUCCESS);

        sub.addChild("one", new TestNode("one", Result.SUCCESS));
        sub.addChild("two", new TestNode("two", Result.SUCCESS));

        root.addChild("sub", sub);

        List<String> results = root.tabComplete(
                sender(),
                new String[]{"sub", "t"},
                0
        );

        assertEquals(1, results.size());
        assertEquals("two", results.get(0));
    }

    @Test
    void tabComplete_returnsEmptyWhenNoMatch() {
        CommandNode root = new TestNode("root", Result.SUCCESS);
        root.addChild("child", new TestNode("child", Result.SUCCESS));

        List<String> results = root.tabComplete(
                sender(),
                new String[]{"unknown"},
                0
        );

        assertTrue(results.isEmpty());
    }
}
