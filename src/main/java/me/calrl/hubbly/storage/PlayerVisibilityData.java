package me.calrl.hubbly.storage;

import me.calrl.hubbly.enums.data.PlayerMovementMode;
import me.calrl.hubbly.enums.data.PlayerVisibilityMode;

public class PlayerVisibilityData {
    private PlayerVisibilityMode mode;

    public PlayerVisibilityData(PlayerVisibilityMode mode) {
        this.mode = mode;
    }

    public PlayerVisibilityMode getMode() {
        return mode;
    }

    public void setMode(PlayerVisibilityMode mode) {
        this.mode = mode;
    }
}
