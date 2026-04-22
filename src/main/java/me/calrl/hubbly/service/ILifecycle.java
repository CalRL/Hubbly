package me.calrl.hubbly.service;

public interface ILifecycle {
    void onEnable();
    void onReload();
    void onDisable();
}
