package me.calrl.hubbly.managers;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.commands.subcommands.*;
import me.calrl.hubbly.interfaces.SubCommand;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SubCommandManager {
    private final Map<String, SubCommand> subCommands;
    private final Hubbly plugin;
    public SubCommandManager(Hubbly plugin) {
        this.plugin = plugin;
        this.subCommands = new HashMap<>();
        this.load();
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
    }

    public void reload() {
        this.clear();
        this.load();
    }

    public Map<String, SubCommand> getSubCommands() {
        return this.subCommands;
    }
}
