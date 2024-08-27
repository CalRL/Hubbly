package me.calrl.hubbly;

public enum Permissions {
    COMMAND_HUBBLY("command"),
    COMMAND_SETSPAWN("command.setspawn"),
    COMMAND_SPAWN("command.spawn"),
    COMMAND_FLY("command.fly"),
    COMMAND_CLEARCHAT("command.clearchat"),
    COMMAND_GIVE("command.give"),
    COMMAND_RELOAD("command.reload"),
    COMMAND_VERSION("command.version"),

    BYPASS_FORCE_INVENTORY("bypass.forceinvenory"),
    BYPASS_ANTI_SWEAR("bypass.antiswear"),
    BYPASS_BLOCKED_COMMANDS("bypass.blockedcommands"),

    // World Event Manager Bypasses

    BYPASS_ITEM_ALL("bypass.item.*"),
    BYPASS_ITEM_DROP("bypass.item.drop"),
    BYPASS_ITEM_PICKUP("bypass.item.pickup"),
    BYPASS_DAMAGE("bypass.damage"),
    BYPASS_BLOCK_PLACE("bypass.place"),
    BYPASS_BLOCK_BREAK("bypass.break"),
    BYPASS_INTERACT("bypass.interact"),
    BYPASS_FOOD_CHANCE("bypass.food"),

    USE_SELECTOR("use.compass"),
    USE_PLAYER_VISIBILITY("use.playervisibility"),
    USE_SOCIALS("use.socials"),
    USE_LAUNCHPAD("use.launchpad");

    private final String permission;

    Permissions(String permission) {
        this.permission = permission;
    }
    public final String getPermission() {
        return "hubbly." + this.permission;
    }
}


