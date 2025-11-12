package me.calrl.hubbly.managers;

import me.calrl.hubbly.Hubbly;

public class ManagerFactory {

    private Hubbly plugin;
    private TridentDataManager tridentDataManager;
    private SpawnTaskManager spawnTaskManager;
    private PlayerVisibilityManager playerVisibilityManager;

    public ManagerFactory(Hubbly plugin) {
        this.plugin = plugin;
        this.start();
    }

    public void start() {
        this.tridentDataManager = new TridentDataManager();
        this.spawnTaskManager = new SpawnTaskManager();
        this.playerVisibilityManager = new PlayerVisibilityManager(this.plugin);
    }

    public TridentDataManager getTridentDataManager() {
        return this.tridentDataManager;
    }

    public SpawnTaskManager getSpawnTaskManager() {
        return this.spawnTaskManager;
    }

    public PlayerVisibilityManager getPlayerVisibilityManager() {
        return this.playerVisibilityManager;
    }

}
