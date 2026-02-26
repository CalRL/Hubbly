package me.calrl.hubbly.service;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.managers.FileManager;
import me.calrl.hubbly.managers.LocaleManager;

import java.util.Locale;

public class ConfigService extends AbstractService {

    private LocaleManager localeManager;
    private FileManager fileManager;
    public ConfigService(Hubbly plugin) {
        super(plugin);
    }


    @Override
    public void onEnable() {
        this.localeManager = register(new LocaleManager(plugin));
        this.fileManager = register(new FileManager(plugin));

        super.onEnable();
    }

    public LocaleManager localeManager() {
        return this.localeManager;
    }
}
