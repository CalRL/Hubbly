package me.calrl.hubbly.storage;

import java.util.Objects;
import java.util.UUID;

public class PlayerData {
    private final UUID uuid;
    private final String name;
    private String doubleJumpNbt;
    private boolean hideMode;

    public PlayerData(UUID uuid, String name, String doubleJumpNbt, boolean hideMode) {
        this.uuid = uuid;
        this.name = name;
        this.doubleJumpNbt = doubleJumpNbt;
        this.hideMode = hideMode;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public String getDoubleJumpNbt() {
        return doubleJumpNbt;
    }

    public boolean isChanged(String doubleJumpNbt) {
        return Objects.equals(doubleJumpNbt, this.doubleJumpNbt);
    }

    public void setDoubleJumpNbt(String doubleJumpNbt) {
        this.doubleJumpNbt = doubleJumpNbt;
    }

    public boolean getHideMode() {
        return hideMode;
    }

    public void setHideMode(boolean hideMode) {
        this.hideMode = hideMode;
    }
}