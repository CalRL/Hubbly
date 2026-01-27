package me.calrl.hubbly.commands.movement;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.enums.Permissions;
import me.calrl.hubbly.enums.PluginKeys;
import me.calrl.hubbly.enums.Result;
import me.calrl.hubbly.enums.data.PlayerMovementMode;
import me.calrl.hubbly.enums.data.PlayerVisibilityMode;
import me.calrl.hubbly.managers.ManagerFactory;
import me.calrl.hubbly.managers.PlayerVisibilityManager;
import me.calrl.hubbly.managers.StorageManager;
import me.calrl.hubbly.storage.PlayerData;
import me.calrl.hubbly.storage.PlayerMovementData;
import me.calrl.hubbly.utils.CommandNode;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

class DoubleJumpNode extends CommandNode {
    private final Hubbly plugin;
    public DoubleJumpNode(Hubbly plugin) {
        super("doublejump");
        this.plugin = plugin;
    }

    @Override
    public Result execute(CommandSender sender, String[] args, int depth) {
        return new Handler(plugin, sender).execute(PlayerMovementMode.DOUBLEJUMP, Permissions.COMMAND_MOVEMENT_DOUBLEJUMP);
    }
}
