package me.calrl.hubbly.storage.state;

import me.calrl.hubbly.enums.NBTKeys;

import java.util.Objects;

public class MovementState {
    private NBTKeys state;
    public MovementState(NBTKeys state) {
        this.state = state;
    }

    public NBTKeys getState() {
        return this.state;
    }

    public boolean isDifferent(NBTKeys other) {
        return !Objects.equals(this.state, other);
    }
}
