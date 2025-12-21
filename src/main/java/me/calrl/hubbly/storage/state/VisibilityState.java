package me.calrl.hubbly.storage.state;

public class VisibilityState {
    private boolean hidden;
    public VisibilityState(boolean hidden) {
        this.hidden = hidden;
    }

    public boolean isHidden() {
        return this.hidden;
    }

    public boolean isDifferent(boolean other) {
        return other != this.hidden;
    }
}
