package me.calrl.hubbly.managers;

import me.calrl.hubbly.Hubbly;

public class ManagerFactory {

    private Hubbly plugin;
    private TridentDataManager tridentDataManager;
    private SpawnTaskManager spawnTaskManager;

    public ManagerFactory(Hubbly plugin) {
        this.start();
    }

    public void start() {
        this.tridentDataManager = new TridentDataManager();
        this.spawnTaskManager = new SpawnTaskManager();
    }

    public TridentDataManager getTridentDataManager() {
        return this.tridentDataManager;
    }

    public SpawnTaskManager getSpawnTaskManager() {
        return this.spawnTaskManager;
    }

}
