package me.calrl.hubbly.enums.data;

public enum PlayerVisibilityMode {
    VISIBLE,
    HIDDEN;

    public PlayerVisibilityMode opposite() {
        return this == VISIBLE ? HIDDEN : VISIBLE;
    }
}
