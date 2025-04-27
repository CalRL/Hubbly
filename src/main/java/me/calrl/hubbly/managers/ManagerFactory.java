package me.calrl.hubbly.managers;

import me.calrl.hubbly.Hubbly;

public class ManagerFactory {

    private Hubbly plugin;
    private TridentDataManager tridentDataManager;

    public ManagerFactory(Hubbly plugin) {
        this.start();
    }

    public void start() {
        this.tridentDataManager = new TridentDataManager();
    }

    public TridentDataManager getTridentDataManager() {
        return this.tridentDataManager;
    }
}
