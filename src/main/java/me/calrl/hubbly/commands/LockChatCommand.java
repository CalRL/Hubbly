package me.calrl.hubbly.commands;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.Permissions;
import me.calrl.hubbly.managers.LockChat;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class LockChatCommand implements CommandExecutor {

    private Hubbly plugin;
    private LockChat lockChat;
    public LockChatCommand(Hubbly plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(!commandSender.hasPermission(Permissions.COMMAND_LOCK_CHAT.getPermission())) return true;
        lockChat = plugin.getLockChat();
        if(lockChat.getChatLock()) {
            Bukkit.broadcastMessage("Chat has been unlocked by " + commandSender.getName());
        } else {
            Bukkit.broadcastMessage("Chat has been locked by " + commandSender.getName());
        }
        lockChat.flipChatLock();

        return true;
    }
}
