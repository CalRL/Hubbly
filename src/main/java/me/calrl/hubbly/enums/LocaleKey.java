package me.calrl.hubbly.enums;

public enum LocaleKey {
    NO_CONSOLE("no_console", "Only players can use this command!"),
    NO_PERMISSION_COMMAND("no_permission_command", "You do not have permission to use this command!"),
    NO_PERMISSION_USE("no_permission_use", "You do not have permission to use this!"),
    RELOAD("reload", "Reloaded Hubbly plugin"),
    FLY_ENABLE("fly.enable", "Flight mode enabled."),
    FLY_DISABLE("fly.disable", "Flight mode disabled."),
    NO_FLY_ENABLED("no_fly_enabled", "Flight is disabled in config"),
    BLOCKED_COMMAND("blocked_command", "Unknown command. Type \"/help\" for help"),
    BLOCKED_MESSAGE("blocked_message", "Please do not use that word!"),
    SUCCESS("success", "Success!"),
    FAILURE("failure", "Failure!"),
    CHAT_LOCKED("chat_locked", "Chat has been locked by %player%"),
    CHAT_UNLOCKED("chat_unlocked", "Chat has been unlocked by %player%"),
    UPDATE_NEW("update.new_update", "An update is available! Hubbly %new% can be downloaded on SpigotMC, you are on %current%"),
    UPDATE_NONE("update.no_update", "Hubbly %current% is up to date"),
    UPDATE_ERROR("update.error", "Could not check for a new version..."),
    UNKNOWN_COMMAND("unknown_command", "Unknown command. Use '/hubbly help' for a list of commands."),
    ANTI_WDL("anti_wdl", "%player% tried to download the world"),
    USAGE_HUBBLY("usage.hubbly", "Usage: /hubbly <command> <args>");

    private final String path;
    private final String defaultMessage;

    LocaleKey(String path, String defaultMessage) {
        this.path = path;
        this.defaultMessage = defaultMessage;
    }

    public String getPath() {
        return path;
    }

    public String getDefault() {
        return defaultMessage;
    }
}
