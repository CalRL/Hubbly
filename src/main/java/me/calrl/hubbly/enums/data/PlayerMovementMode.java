package me.calrl.hubbly.enums.data;

import java.util.Optional;

public enum PlayerMovementMode {
    NONE("NONE"),
    DOUBLEJUMP("DOUBLEJUMP"),
    FLY("FLY");

    private String key;
    PlayerMovementMode(String s) {
        this.key = s;
    }

    public String getString() {
        return this.key;
    }

    public static Optional<PlayerMovementMode> fromString(String value) {
        if (value == null) {
            return Optional.empty();
        }

        for (PlayerMovementMode mode : values()) {
            if (mode.name().equals(value) || mode.getString().equals(value)) {
                return Optional.of(mode);
            }
        }

        return Optional.empty();
    }
}
