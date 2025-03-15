package me.calrl.hubbly.commands.subcommands;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.interfaces.SubCommand;
import me.calrl.hubbly.managers.FileManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;

public class LoadItemCommand implements SubCommand {
    private final Hubbly plugin;
    public LoadItemCommand(Hubbly plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public String getUsage() {
        return "";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cOnly players can use this command!");
            return;
        }

        FileManager manager = new FileManager(plugin);
        File file = new File(manager.getPath() + "saved_item.yml");
        ItemStack itemStack = manager.loadItemStack(file);
        if (itemStack == null) {
            sender.sendMessage("§cFailed to load item.");
            return;
        }

        player.getInventory().addItem(itemStack);
        sender.sendMessage("§aItem successfully loaded and added to your inventory!");
    }
}
