package me.calrl.hubbly.commands.subcommands;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.enums.Permissions;
import me.calrl.hubbly.interfaces.SubCommand;
import org.bukkit.entity.Player;

public class GetLockChatCommand implements SubCommand {
    private Hubbly plugin;
    public GetLockChatCommand(Hubbly plugin) {
        this.plugin = plugin;
    }
    @Override
    public String getIdentifier() {
        return "GETLOCKCHAT";
    }

    @Override
    public void execute(Player player, String[] args) {
        if(player.hasPermission(Permissions.BYPASS_CHAT_LOCK.getPermission())) {
            player.sendMessage(String.valueOf(plugin.getLockChat().getChatLock()));
        }
    }
}
