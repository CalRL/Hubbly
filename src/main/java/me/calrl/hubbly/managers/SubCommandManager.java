package me.calrl.hubbly.managers;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.commands.debug.DebugCommand;
import me.calrl.hubbly.commands.subcommands.*;
import me.calrl.hubbly.commands.worlds.WorldsCommand;
import me.calrl.hubbly.interfaces.SubCommand;
import me.calrl.hubbly.utils.CommandNode;

import java.util.HashMap;
import java.util.Map;

public class SubCommandManager {
    private final Map<String, SubCommand> subCommands;
    private final Map<String, CommandNode> nodes;
    private final Hubbly plugin;
    public SubCommandManager(Hubbly plugin) {
        this.plugin = plugin;
        this.subCommands = new HashMap<>();
        this.nodes = new HashMap<>();

        this.load();
        this.loadNodes();
    }

    public void register(SubCommand subCommand) {
        this.subCommands.put(subCommand.getName(), subCommand);
    }

    public void clear() {
        this.subCommands.clear();
    }

    public void load() {
        if(plugin == null) {
            throw new NullPointerException("SubCommands registration returned null... Report to developer");
        }
        this.register(new ReloadCommand(plugin));
        this.register(new VersionCommand(plugin));
        this.register(new NextAnnouncementCommand(plugin));
        this.register(new GiveCommand(plugin));
        this.register(new HelpCommand(plugin));
        this.register(new ConvertCommand(plugin));
        this.register(new MenuCommand(plugin));
        new DebugMode().info("Loaded all SubCommands");
    }

    public void reload() {
        this.clear();
        this.load();
    }

    public Map<String, SubCommand> getSubCommands() {
        return this.subCommands;
    }

    public void loadNodes() {
        this.registerNode(new WorldsCommand(plugin));
        this.registerNode(new DebugCommand(plugin));
        new DebugMode().info("Loaded all WorldsCommand nodes");
    }
    public void registerNode(CommandNode node) {
        this.nodes.put(node.getIdentifier(), node);
    }

    public Map<String, CommandNode> getNodes() { return this.nodes; }
}
