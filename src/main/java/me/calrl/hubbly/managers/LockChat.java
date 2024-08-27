package me.calrl.hubbly.managers;

import me.calrl.hubbly.Hubbly;

public class LockChat {
    private final Hubbly plugin;
    private boolean isChatLocked;
    public LockChat(Hubbly plugin) {
        this.plugin = plugin;
        isChatLocked = plugin.getConfig().getBoolean("lock_chat", false);
    }


    public void setChatLock(Boolean bool) {
        plugin.getConfig().set("lock_chat", bool);
        plugin.saveConfig();
        isChatLocked = plugin.getConfig().getBoolean("lock_chat", false);
    }

    public void flipChatLock() {
        isChatLocked = !isChatLocked;

        plugin.getConfig().set("lock_chat", isChatLocked);
        plugin.saveConfig();
    }

    public boolean getChatLock() {
        return isChatLocked;
    }
}
