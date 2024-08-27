package me.calrl.hubbly.commands.subcommands;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.enums.Permissions;
import me.calrl.hubbly.enums.PluginKeys;
import me.calrl.hubbly.interfaces.SubCommand;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class GetPDCCommand implements SubCommand {

    private Hubbly plugin;
    public GetPDCCommand(Hubbly plugin){
        this.plugin = plugin;
    }
    @Override
    public String getIdentifier() {
        return "GETPDC";
    }

    @Override
    public void execute(Player player, String[] args) {
        if(player.hasPermission(Permissions.COMMAND_ADMIN.getPermission())) {
            NamespacedKey key = PluginKeys.PLAYER_VISIBILITY.getKey();
            ItemStack item = player.getInventory().getItemInMainHand();
            if(item != null && !item.getType().equals(Material.AIR)) {
               String string = item.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING);
                if(string != null) {
                    player.sendMessage(string);
                } else {
                    player.sendMessage("null");
                }
            }

        }
    }
}
