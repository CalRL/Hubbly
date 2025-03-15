package me.calrl.hubbly.commands.subcommands;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.interfaces.SubCommand;
import me.calrl.hubbly.managers.FileManager;
import net.minecraft.nbt.NBTTagCompound;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_20_R4.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class SaveItemCommand implements SubCommand {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final Hubbly plugin;
    public SaveItemCommand(Hubbly plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "saveitem";
    }

    @Override
    public String getDescription() {
        return "Saves current item to a json file";
    }

    @Override
    public String getUsage() {
        return "/hubbly save";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cOnly players can use this command!");
            return;
        }

        ItemStack itemStack = player.getInventory().getItemInMainHand();
        if (itemStack == null || itemStack.getType().isAir()) {
            sender.sendMessage("§cYou must hold an item to save it.");
            return;
        }

        FileManager manager = new FileManager(plugin);
        File file = manager.createFile("saved_item.yml");
        manager.saveItemStack(itemStack, file);
        sender.sendMessage("§aItem saved successfully to " + file.getPath());
    }


}
