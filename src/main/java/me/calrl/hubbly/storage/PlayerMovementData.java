package me.calrl.hubbly.storage;

import me.calrl.hubbly.enums.data.PlayerMovementMode;
import me.calrl.hubbly.enums.data.PlayerVisibilityMode;
import me.calrl.hubbly.listeners.player.PlayerMoveListener;

public class PlayerMovementData {

    private PlayerMovementMode mode;

    public PlayerMovementData(PlayerMovementMode mode) {
        this.mode = mode;
    }

    public PlayerMovementMode getMode() {
        return mode;
    }

    public void setMode(PlayerMovementMode mode) {
        this.mode = mode;
    }
}
