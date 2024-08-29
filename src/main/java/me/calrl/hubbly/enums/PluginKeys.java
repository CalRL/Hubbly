package me.calrl.hubbly.enums;

import me.calrl.hubbly.Hubbly;
import org.bukkit.NamespacedKey;

import javax.naming.Name;

public enum PluginKeys {

    PLAYER_VISIBILITY(new NamespacedKey(Hubbly.getInstance(), "player_visibility")),
    SOCIALS(new NamespacedKey(Hubbly.getInstance(), "socials")),
    SELECTOR(new NamespacedKey(Hubbly.getInstance(), "selector"));


    private final NamespacedKey key;
    PluginKeys(NamespacedKey key) {
        this.key = key;
    }

    public NamespacedKey getKey() {
        return key;
    }

}
