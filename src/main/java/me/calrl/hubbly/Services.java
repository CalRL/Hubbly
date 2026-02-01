package me.calrl.hubbly;

import me.calrl.hubbly.interfaces.ILifecycle;

public class ServiceRegistry implements ILifecycle {
    private final Hubbly plugin;
    public ServiceRegistry(Hubbly plugin) {
        this.plugin = plugin;

    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onReload() {

    }

    @Override
    public void onDisable() {

    }
}
