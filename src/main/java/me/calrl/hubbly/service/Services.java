package me.calrl.hubbly.service;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.action.ActionManager;
import me.calrl.hubbly.managers.*;
import me.calrl.hubbly.managers.cooldown.CooldownManager;
import me.calrl.hubbly.utils.AntiWDLSetup;
import me.calrl.hubbly.utils.update.UpdateUtil;

public class Services extends AbstractService {

    private ResourceService resourceService;

    private LockChat lockChat;
    private TridentDataManager tridentManager;
    private SpawnTaskManager spawnTaskManager;
    private UpdateUtil updateUtil;
    private PlayerVisibilityManager playerVisibilityManager;
    private DisabledWorlds disabledWorlds;
    private CooldownManager cooldownManager;
    private LocaleManager localeManager;
    public Services(Hubbly plugin) {
        super(plugin);
    }

    @Override
    public void onEnable() {
        this.resourceService = register(new ResourceService(plugin));
        this.updateUtil = register(new UpdateUtil(plugin));

        this.lockChat = register(new LockChat(plugin));
        this.tridentManager = register(new TridentDataManager());
        this.spawnTaskManager = register(new SpawnTaskManager());
        this.playerVisibilityManager = register(new PlayerVisibilityManager(this.plugin));
        this.disabledWorlds = register(new DisabledWorlds(plugin));
        this.cooldownManager = register(new CooldownManager());

        new AntiWDLSetup(this.plugin, this.plugin.getConfig());

        super.onEnable();
    }

    public ResourceService resources() {
        return this.resourceService;
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
    public CooldownManager cooldowns() { return this.cooldownManager; }
}