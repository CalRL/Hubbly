package me.calrl.hubbly.managers;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.storage.Database;

public class ManagerFactory {

    private Hubbly plugin;
    private TridentDataManager tridentDataManager;
    private SpawnTaskManager spawnTaskManager;
    private PlayerVisibilityManager playerVisibilityManager;
    private DatabaseManager databaseManager;

    public ManagerFactory(Hubbly plugin) {
        this.plugin = plugin;
        this.start();
    }

    public void start() {
        this.tridentDataManager = new TridentDataManager();
        this.spawnTaskManager = new SpawnTaskManager();
        this.playerVisibilityManager = new PlayerVisibilityManager(this.plugin);

        if(plugin.getConfig().getBoolean("database.enabled")) {
            plugin.getLogger().info("Starting database manager");
            DatabaseManager mg = new DatabaseManager(plugin);
            mg.start(plugin);
            this.databaseManager = mg;
        }

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
