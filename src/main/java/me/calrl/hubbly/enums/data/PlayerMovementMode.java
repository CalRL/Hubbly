package me.calrl.hubbly.enums.data;

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
}
