package me.calrl.hubbly.storage;

import java.util.UUID;

public class PlayerData {
    private final UUID uuid;
    private final String name;
    private String doubleJumpNbt;
    private String playerVisibilityNbt;

    public PlayerData(UUID uuid, String name, String doubleJumpNbt, String playerVisibilityNbt) {
        this.uuid = uuid;
        this.name = name;
        this.doubleJumpNbt = doubleJumpNbt;
        this.playerVisibilityNbt = playerVisibilityNbt;
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

    public void setDoubleJumpNbt(String doubleJumpNbt) {
        this.doubleJumpNbt = doubleJumpNbt;
    }

    public String getPlayerVisibilityNbt() {
        return playerVisibilityNbt;
    }

    public void setPlayerVisibilityNbt(String playerVisibilityNbt) {
        this.playerVisibilityNbt = playerVisibilityNbt;
    }
}