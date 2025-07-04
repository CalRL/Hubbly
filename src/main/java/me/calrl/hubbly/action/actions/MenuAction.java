package me.calrl.hubbly.action.actions;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.action.Action;
import me.calrl.hubbly.inventory.InventoryBuilder;
import me.calrl.hubbly.managers.FileManager;
import me.calrl.hubbly.utils.MessageBuilder;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class MenuAction implements Action {
    @Override
    public String getIdentifier() {
        return "MENU";
    }

    @Override
    public void execute(Hubbly plugin, Player player, String data) {
        FileManager manager = plugin.getFileManager();
        String path = data;
        if(!data.startsWith("menus/")) {
            path = "menus/" + data;
        }

        plugin.getDebugMode().info(path);
        FileConfiguration config = manager.getConfig(path);

        String perm = config.getString("permission");

        if(perm != null && !player.hasPermission(perm)) {
            new MessageBuilder()
                    .setPlugin(plugin)
                    .setPlayer(player)
                    .setKey("no_permission_use")
                    .send();
            return;
        }

        Inventory inventory = new InventoryBuilder()
                .setPlayer(player)
                .fromFile(config)
                .getInventory();

        player.openInventory(inventory);
    }
}
