package me.calrl.hubbly.service;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.managers.*;
import me.calrl.hubbly.utils.update.UpdateUtil;

public class Services extends AbstractService {

    private ConfigService configService;

    private LockChat lockChat;
    private TridentDataManager tridentManager;
    private SpawnTaskManager spawnTaskManager;
    private UpdateUtil updateUtil;
    private PlayerVisibilityManager playerVisibilityManager;
    private DisabledWorlds disabledWorlds;
    public Services(Hubbly plugin) {
        super(plugin);
    }

    @Override
    public void onEnable() {
        this.configService = register(new ConfigService(plugin));
        this.updateUtil = register(new UpdateUtil(plugin));

        this.lockChat = register(new LockChat(plugin));
        this.tridentManager = register(new TridentDataManager());
        this.spawnTaskManager = register(new SpawnTaskManager());
        this.playerVisibilityManager = register(new PlayerVisibilityManager(this.plugin));
        this.disabledWorlds = register(new DisabledWorlds(plugin));

        super.onEnable();
    }

    public ConfigService config() {
        return this.configService;
    }

    public UpdateUtil updateUtil() {
        return this.updateUtil;
    }

    public LockChat lockChat() {
        return this.lockChat;
    }

    public TridentDataManager tridentDataManager() {
        return this.tridentManager;
    }

    public SpawnTaskManager spawnTaskManager() {
        return this.spawnTaskManager;
    }

    public PlayerVisibilityManager playerVisibilityManager() {
        return this.playerVisibilityManager;
    }

    public DisabledWorlds disabledWorlds() {
        return this.disabledWorlds;
    }
}