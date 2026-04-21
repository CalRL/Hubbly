package me.calrl.hubbly.managers;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.enums.LocaleKey;
import me.calrl.hubbly.service.ILifecycle;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class LocaleManager implements ILifecycle {

    private final Map<String, FileConfiguration> locales = new HashMap<>();
    private FileManager fileManager;
    private FileConfiguration fallback;
    private String defaultLanguage;
    private Hubbly plugin;
    public LocaleManager(Hubbly plugin) {
        this.plugin = plugin;
        this.fileManager = plugin.services().resources().fileManager();
    }

    public void loadFallback() {
        FileConfiguration config = plugin.getConfig();
        String defaultLanguage = config.getString("default_language", "en");
        String path = "languages/" + defaultLanguage + ".yml";
        this.defaultLanguage = defaultLanguage;
        this.fallback = locales.getOrDefault(defaultLanguage, fileManager.getConfig(path));
    }

    public String get(Player player, String path) {
        if(path == null || path.isEmpty()) {
            throw new NullPointerException("Path returned null");
        }

        String locale = player.getLocale().split("_")[0].toLowerCase();
        FileConfiguration config = locales.getOrDefault(locale, fallback);

        return config.getString(path, "Missing message: " + path);
    }

    public String get(String language, String path) {
        FileConfiguration config = locales.getOrDefault(language.toLowerCase(), fallback);
        return config.getString(path, "Missing message: " + path);
    }

    private String getOrDefault(FileConfiguration config, LocaleKey key) {
        String value = config.getString(key.getPath());
        return (value != null) ? value : key.getDefault();
    }

    public void loadAllLocales() {
        this.saveDefaultLanguages();

        File folder = new File(this.plugin.getDataFolder(), "languages");
        if (!folder.exists()) folder.mkdirs();

        File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".yml"));
        if (files == null) return;

        for (File file : files) {
            String fileName = file.getName().replace(".yml", "").toLowerCase();
            FileConfiguration config = fileManager.getConfig("languages/" + file.getName());
            locales.put(fileName, config);
            plugin.getDebugMode().info("Loaded language " + fileName);
        }
    }

    public void clear() {
        this.locales.clear();
    }

    public void reload() {
        this.clear();
        this.loadAllLocales();
        this.loadFallback();
    }

    public FileConfiguration getFallback() {
        return this.fallback;
    }

    public String getDefaultLanguage() {
        return this.defaultLanguage;
    }

    private void saveDefaultLanguages() {
        String[] languages = { "en.yml" };

        for (String lang : languages) {
            File file = new File(this.plugin.getDataFolder(), "languages/" + lang);
            if (!file.exists()) {
                this.plugin.saveResource("languages/" + lang, false);
            }
        }
    }

    @Deprecated(forRemoval = true)
    public String getLocaleMessage(Player player, String key) {
        return this.get(player, key);
    }


    @Override
    public void onEnable() {
        this.loadAllLocales();
        this.loadFallback();
    }

    @Override
    public void onReload() {
        this.clear();
        this.onEnable();
    }

    @Override
    public void onDisable() {

    }
}
