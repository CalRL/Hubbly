package me.calrl.hubbly.service;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.managers.FileManager;
import me.calrl.hubbly.managers.LocaleManager;

public class ResourceService extends AbstractService {

    private LocaleManager localeManager;
    private FileManager fileManager;
    public ResourceService(Hubbly plugin) {
        super(plugin);
    }


    @Override
    public void onEnable() {
        this.fileManager = register(new FileManager(plugin));
        this.localeManager = register(new LocaleManager(plugin));

        super.onEnable();
    }

    public LocaleManager localeManager() {
        return this.localeManager;
    }

    public FileManager fileManager() {
        return this.fileManager;
    }
}
