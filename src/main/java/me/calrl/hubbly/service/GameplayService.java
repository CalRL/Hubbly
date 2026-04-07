package me.calrl.hubbly.service;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.managers.*;
import net.minecraft.world.item.Item;

public class GameplayService extends AbstractService {
    private ItemsManager itemsManager;
    private BossBarManager bossBarManager;
    private SubCommandManager subCommandManager;
    public GameplayService(Hubbly plugin) {
        super(plugin);
    }
    @Override
    public void onEnable() {
        this.itemsManager = register(new ItemsManager(this.plugin));
        this.bossBarManager = register(new BossBarManager(this.plugin));
        this.subCommandManager = register(new SubCommandManager(this.plugin));

        this.bossBarManager.reAddAllBossBars();
        super.onEnable();
    }

    public ItemsManager itemsManager() {
        return this.itemsManager;
    }

    public BossBarManager bossBarManager() {
        return this.bossBarManager;
    }

    public SubCommandManager subCommandManager() {
        return this.subCommandManager;
    }


}
