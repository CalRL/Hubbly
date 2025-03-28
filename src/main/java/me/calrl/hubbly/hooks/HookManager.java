package me.calrl.hubbly.hooks;

import me.calrl.hubbly.Hubbly;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class HookManager {
    private Hubbly plugin;
    private Map<String, Hook> hooks;
    public HookManager(Hubbly plugin) {
        this.plugin = plugin;
        this.hooks = new HashMap<>();

        if(Bukkit.getPluginManager().isPluginEnabled("HeadDatabase")) {
            hooks.put("HEAD_DATABASE", new HeadDatabaseHook());
            plugin.getLogger().info(" Hooked into HeadDatabase");
        } else {
            plugin.getLogger().info("Couldn't hook into HDB");
        }

        hooks
                .values()
                .stream()
                .filter(Objects::nonNull)
                .forEach(hook -> {
                    hook.onEnable(plugin);
                    plugin.getLogger().info("Sent enable to " + hook);
                });
    }

    public boolean isHookEnabled(String id) {
        return hooks.containsKey(id);
    }

    public Hook getHook(String id) {
        return hooks.get(id);
    }

}
