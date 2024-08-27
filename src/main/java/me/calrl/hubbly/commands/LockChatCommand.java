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
    public LockChatCommand(Hubbly plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(!commandSender.hasPermission(Permissions.COMMAND_LOCK_CHAT.getPermission())) return true;
        plugin.getLockChat().flipChatLock();
        Bukkit.broadcastMessage("Chat has been locked by " + commandSender.getName());
        return true;
    }
}
