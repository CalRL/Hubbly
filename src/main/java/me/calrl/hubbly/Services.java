package me.calrl.hubbly;

import me.calrl.hubbly.interfaces.ILifecycle;

public class Services implements ILifecycle {
    private final Hubbly plugin;
    public Services(Hubbly plugin) {
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
