package me.calrl.hubbly.utils;

import org.bukkit.command.CommandSender;

import java.util.*;
import java.util.stream.Collectors;

public abstract class CommandNode {
    protected final String identifier;
    protected final Map<String, CommandNode> children = new HashMap<>();
    public CommandNode(String identifier) {
        this.identifier = identifier;
    }

    public void addChild(String identifier, CommandNode child) {
        this.children.put(identifier.toLowerCase(), child);
    }

    public boolean matches(String input) {
        return this.identifier.equalsIgnoreCase(input);
    }
    public abstract void execute(CommandSender sender, String[] args, int depth);

    public List<String> tabComplete(CommandSender sender, String[] args, int depth) {
        if (args.length == depth + 1) {
            return children.keySet().stream()
                    .filter(key -> key.startsWith(args[depth].toLowerCase()))
                    .collect(Collectors.toList());
        }

        String next = args[depth].toLowerCase();
        CommandNode child = children.get(next);
        if (child != null) {
            return child.tabComplete(sender, args, depth + 1);
        }

        return Collections.emptyList();
    }
    public String getIdentifier() { return this.identifier; }

    public Map<String, CommandNode> getChildren() {
        return children;
    }
}
