package me.calrl.hubbly.enums;

public enum NBTKeys {
    NONE("NONE"),
    DOUBLEJUMP("DOUBLEJUMP"),
    FLY("FLY"),
    ;

    NBTKeys(String s) {
        this.val = s;
    }
    private String val;

    public String get() {
        return this.val;
    }
}
