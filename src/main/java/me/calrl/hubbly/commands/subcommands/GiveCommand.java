package me.calrl.hubbly.commands.subcommands;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.enums.Permissions;
import me.calrl.hubbly.interfaces.CustomItem;
import me.calrl.hubbly.interfaces.SubCommand;
import me.calrl.hubbly.managers.DebugMode;
import me.calrl.hubbly.managers.ItemsManager;
import me.calrl.hubbly.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class GiveCommand implements SubCommand {

    private Hubbly plugin;
    private FileConfiguration config;
    private Map<String, CustomItem> items = new HashMap<>();
    private ItemsManager itemsManager;
    private DebugMode debugMode;


    public GiveCommand(Hubbly plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        this.debugMode = plugin.getDebugMode();
        this.itemsManager = plugin.getItemsManager();
    }


    @Override
    public String getIdentifier() {
        return "GIVE";
    }

    @Override
    public void execute(Player player, String[] args) {
        items = itemsManager.getItems();

        if (args.length < 3) {
            player.sendMessage(ChatColor.YELLOW + "Usage: /give <player> <item> [amount] [slot]");
            return;
        }

        if(!player.hasPermission(Permissions.COMMAND_GIVE.getPermission())) {
            player.sendMessage(ChatUtils.translateHexColorCodes(
                    config.getString("messages.no_permission_use", "No permission")
            ));
            return;
        }

        Player targetPlayer = Bukkit.getPlayer(args[1]);
        if (targetPlayer == null) {
            player.sendMessage(ChatColor.RED + "Player not found: " + args[1]);
            return;
        }

        String itemName = ChatColor.stripColor(args[2].toLowerCase());
        CustomItem customItem = items.get(itemName);

        if (customItem == null) {
            player.sendMessage(ChatColor.RED + "Unknown item: " + itemName);
            return;
        }

        ItemStack item = customItem.createItem();
        if(args.length == 4) {
            int amount = Integer.parseInt(args[3]);
            item.setAmount(amount);
            targetPlayer.getInventory().addItem(item);
            return;
        }


        if (args.length == 5) {
            try {
                int amount = Integer.parseInt(args[3]);
                item.setAmount(amount);

                int slot = Integer.parseInt(args[4]);
                if (slot < 0 || slot >= targetPlayer.getInventory().getSize()) {
                    player.sendMessage(ChatColor.RED + "Invalid slot: " + args[4]);
                    return;
                }

                targetPlayer.getInventory().setItem(slot-1, item);
                player.sendMessage(ChatColor.YELLOW + "Given " + itemName + " to " + targetPlayer.getName() + " in slot " + slot);
            } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.RED + "Slot must be a number: " + args[4]);
            }
        } else {
            targetPlayer.getInventory().addItem(item);
            debugMode.info("Given " + itemName + " to " + targetPlayer.getName());
        }
    }


}
