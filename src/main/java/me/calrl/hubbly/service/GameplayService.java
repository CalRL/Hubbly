package me.calrl.hubbly.service;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.managers.LockChat;
import me.calrl.hubbly.managers.TridentDataManager;

public class GameplayService extends AbstractService {
    private LockChat lockChat;
    private TridentDataManager tridentManager;
    public GameplayService(Hubbly plugin) {
        super(plugin);
    }
    @Override
    public void onEnable() {
        this.lockChat = register(new LockChat(plugin));
        this.tridentManager = register(new TridentDataManager());

        super.onEnable();
    }


    public LockChat lockChat() {
        return this.lockChat;
    }

    public TridentDataManager tridentDataManager() {
        return this.tridentManager;
    }


}
