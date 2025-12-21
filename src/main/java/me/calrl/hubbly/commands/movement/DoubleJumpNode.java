package me.calrl.hubbly.commands.movement;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.enums.NBTKeys;
import me.calrl.hubbly.enums.Result;
import me.calrl.hubbly.managers.ManagerFactory;
import me.calrl.hubbly.storage.PlayerData;
import me.calrl.hubbly.utils.CommandNode;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class DoubleJumpNode extends CommandNode {
    private final Hubbly plugin;
    public DoubleJumpNode(Hubbly plugin) {
        super("doublejump");
        this.plugin = plugin;
    }
    @Override
    public Result execute(CommandSender sender, String[] args, int depth) {
        plugin.getDebugMode().info("Running doublejumpnnode");
        if(!(sender instanceof Player player)) return Result.PLAYER_ONLY;
        PersistentDataContainer container = player.getPersistentDataContainer();
        NamespacedKey key = NamespacedKey.fromString("hubbly.movement");
        if(key == null) return Result.FAILURE;
        container.set(key, PersistentDataType.STRING, NBTKeys.DOUBLEJUMP.get());

        ManagerFactory factory = plugin.getManagerFactory();
        PlayerData data = factory.getRepository().get(player.getUniqueId());
        data.setDoubleJumpNbt(NBTKeys.DOUBLEJUMP.get());
        factory.getRepository().update(player.getUniqueId(), data);
        return Result.SUCCESS;
    }
}
