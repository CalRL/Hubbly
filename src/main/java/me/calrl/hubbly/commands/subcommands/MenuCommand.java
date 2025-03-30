package me.calrl.hubbly.commands.subcommands;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.enums.Permissions;
import me.calrl.hubbly.interfaces.SubCommand;
import me.calrl.hubbly.inventory.InventoryBuilder;
import me.calrl.hubbly.managers.FileManager;
import me.calrl.hubbly.utils.MessageBuilder;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class MenuCommand implements SubCommand {
    private Hubbly plugin;
    public MenuCommand(Hubbly plugin) {
        this.plugin = plugin;
    }
    @Override
    public String getName() {
        return "menu";
    }

    @Override
    public String getDescription() {
        return "Opens menus";
    }

    @Override
    public String getUsage() {
        return "/hubbly menu <menu>";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission(Permissions.COMMAND_MENU.getPermission())) {
            new MessageBuilder(plugin)
                    .setPlayer(sender)
                    .setKey("no_permission_command")
                    .send();
            return;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage("§cThis command can only be used by players.");
            return;
        }

        Player player = (Player) sender;

        if (args.length < 2) {
            player.sendMessage("§cUsage: /hubbly menu <menu>");
            return;
        }

        String menuName = args[1];
        FileManager fileManager = plugin.getFileManager();
        String filePath = "menus/" + menuName + ".yml";
        FileConfiguration menuConfig = fileManager.getConfig(filePath);

        if (menuConfig == null) {
            player.sendMessage("§cMenu not found: " + menuName);
            return;
        }

        InventoryBuilder inventoryBuilder = new InventoryBuilder()
                .setPlayer(player)
                .fromFile(menuConfig);

        player.openInventory(inventoryBuilder.getInventory());
        player.sendMessage("§aOpened menu: " + menuName);
    }

    @Override
    public List<String> tabComplete(Player player, String[] args) {
        if (args.length == 1) {
            FileManager fileManager = plugin.getFileManager();
            return fileManager.listFilesInFolder("menus");
        }
        return Collections.emptyList();
    }

}
