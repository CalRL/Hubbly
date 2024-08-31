package me.calrl.hubbly.enums;

import org.bukkit.Sound;

public enum TridentSounds {
    RIPTIDE_1(Sound.ITEM_TRIDENT_RIPTIDE_1),
    RIPTIDE_2(Sound.ITEM_TRIDENT_RIPTIDE_2),
    RIPTIDE_3(Sound.ITEM_TRIDENT_RIPTIDE_3);

    private final Sound sound;
    TridentSounds(Sound sound) {
        this.sound = sound;
    }
    public Sound getSound() {
        return sound;
    }
}
