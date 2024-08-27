package me.calrl.hubbly.managers;

import me.calrl.hubbly.Hubbly;

public class LockChat {
    private final Hubbly plugin;
    private boolean isChatLocked;
    public LockChat(Hubbly plugin) {
        this.plugin = plugin;
        isChatLocked = false;
    }
    public void setChatLocked(Boolean bool) {
        isChatLocked = bool;
    }
    public void flipChatLock() {
        isChatLocked = !isChatLocked;
    }

    public boolean getChatLock() {
        return isChatLocked;
    }
}
