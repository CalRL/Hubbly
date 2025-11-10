package me.calrl.hubbly.enums;

public enum Result {
    SUCCESS,
    FAILURE,
    INVALID_ARGS,
    NO_PERMISSION,
    USAGE_PRINTED,
    PLAYER_ONLY,
    CONSOLE_ONLY,
    COOLDOWN,
    ALREADY_EXISTS,
    NOTHING_TO_DO,
    NO_CHILD,
    // in disabled world
    DISABLED_WORLD,
    // event cancelled
    CANCELLED,
    NOT_FOUND;

    public static Result from(boolean bool) {
        if(bool) {
            return Result.SUCCESS;
        } else {
            return Result.FAILURE;
        }
    }
}
