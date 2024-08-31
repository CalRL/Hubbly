package me.calrl.hubbly.enums;

import me.calrl.hubbly.Hubbly;
import org.bukkit.NamespacedKey;

import javax.naming.Name;

public enum PluginKeys {

    PLAYER_VISIBILITY(new NamespacedKey(Hubbly.getInstance(), "player_visibility")),
    SOCIALS(new NamespacedKey(Hubbly.getInstance(), "socials")),
    SELECTOR(new NamespacedKey(Hubbly.getInstance(), "selector")),
    TRIDENT(new NamespacedKey(Hubbly.getInstance(), "trident")),
    FISHING_ROD(new NamespacedKey(Hubbly.getInstance(), "fishing_rod")),
    ENDER_BOW(new NamespacedKey(Hubbly.getInstance(),"ender_bow"));



    private final NamespacedKey key;
    PluginKeys(NamespacedKey key) {
        this.key = key;
    }

    public NamespacedKey getKey() {
        return key;
    }

}
