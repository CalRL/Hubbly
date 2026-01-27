package me.calrl.hubbly.commands.movement;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.enums.Permissions;
import me.calrl.hubbly.enums.Result;
import me.calrl.hubbly.enums.data.PlayerMovementMode;
import me.calrl.hubbly.handlers.PlayerMovementHandler;
import me.calrl.hubbly.managers.StorageManager;
import me.calrl.hubbly.storage.PlayerData;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

class Handler {
    private final Hubbly plugin;
    private final CommandSender sender;
    public Handler(Hubbly plugin, CommandSender sender) {
        this.plugin = plugin;
        this.sender = sender;
    }

    public Result execute(PlayerMovementMode mode, Permissions permission) {
        plugin.getDebugMode().info(String.format("Running handler for mode: %s", mode.toString()));

        if(!(sender instanceof Player player)) return Result.PLAYER_ONLY;
        if(!player.hasPermission(permission.getPermission())) return Result.NO_PERMISSION;

        PlayerMovementHandler handler = new PlayerMovementHandler(player, plugin);
        handler.setMovementMode(mode);

        StorageManager storage = plugin.getStorageManager();
        FileConfiguration config = plugin.getConfig();

        if(config.getBoolean("database.enabled") && storage.isActive()) {
            PlayerData data = PlayerData.from(player);
            storage.enqueueSave(data);
        }

        return Result.SUCCESS;
    }
}
