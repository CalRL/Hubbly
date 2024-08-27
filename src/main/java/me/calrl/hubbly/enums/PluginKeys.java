package me.calrl.hubbly.enums;

import me.calrl.hubbly.Hubbly;
import org.bukkit.NamespacedKey;

public enum PluginKeys {
    PLAYER_VISIBILITY(new NamespacedKey(Hubbly.getInstance(), "player_visibility"));

    private final NamespacedKey key;
    PluginKeys(NamespacedKey key) {
        this.key = key;
    }

    public NamespacedKey getKey() {
        return key;
    }

}
