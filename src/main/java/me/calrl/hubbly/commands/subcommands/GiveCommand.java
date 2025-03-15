package me.calrl.hubbly.commands.subcommands;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.action.Action;
import me.calrl.hubbly.action.ActionManager;
import me.calrl.hubbly.enums.Permissions;
import me.calrl.hubbly.interfaces.CustomItem;
import me.calrl.hubbly.interfaces.SubCommand;
import me.calrl.hubbly.managers.DebugMode;
import me.calrl.hubbly.managers.ItemsManager;
import me.calrl.hubbly.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class GiveCommand implements SubCommand {

    private final Hubbly plugin;
    private final Map<String, CustomItem> items;
    private final DebugMode debugMode;

    public GiveCommand(Hubbly plugin) {
        this.plugin = plugin;
        this.items = plugin.getItemsManager().getItems();
        this.debugMode = plugin.getDebugMode();
    }

    @Override
    public String getName() {
        return "give";
    }

    @Override
    public String getDescription() {
        return "Gives a custom item to a player.";
    }

    @Override
    public String getUsage() {
        return "/hubbly give <player> <item> [amount] [slot]";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission(Permissions.COMMAND_GIVE.getPermission())) {
            sender.sendMessage(ChatUtils.translateHexColorCodes(
                    plugin.getConfig().getString("messages.no_permission_use", "No permission")
            ));
            return;
        }
        List<String> arguments = new ArrayList<>(Arrays.asList(args));
        arguments.removeIf(arg -> arg.equalsIgnoreCase("give"));

        // Argument check
        if (arguments.size() < 2) {
            sender.sendMessage(ChatColor.YELLOW + "Usage: " + getUsage());
            return;
        }

        // Find the target player
        String playerName = arguments.getFirst();
        Player targetPlayer = Bukkit.getPlayer(playerName);
        if (targetPlayer == null) {
            sender.sendMessage(ChatColor.RED + "Player not found: " + playerName);
            return;
        }

        // Find the item
        String itemArg = arguments.get(1);
        String itemName = ChatColor.stripColor(itemArg.toLowerCase());
        CustomItem customItem = items.get(itemName);
        if (customItem == null) {
            sender.sendMessage(ChatColor.RED + "Unknown item: " + itemName);
            return;
        }

        ItemStack item = customItem.createItem();
        int amount = 1;
        if (arguments.size() >= 3) {
            try {
                String amountString = arguments.get(2);
                amount = Integer.parseInt(amountString);
                if (amount < 1 || amount > item.getMaxStackSize()) {
                    sender.sendMessage(ChatColor.RED + "Invalid amount: " + amountString);
                    return;
                }
                item.setAmount(amount);
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "Amount must be a number.");
                return;
            }
        }

        // If slot argument is given
        if (arguments.size() == 4) {
            try {
                String slotString = arguments.get(3);
                int slot = Integer.parseInt(slotString) - 1;
                if (slot < 0 || slot >= targetPlayer.getInventory().getSize()) {
                    sender.sendMessage(ChatColor.RED + "Invalid slot: " + slotString);
                    return;
                }
                targetPlayer.getInventory().setItem(slot, item);
                sender.sendMessage(ChatColor.YELLOW + "Given " + amount + " " + itemName + " to " + targetPlayer.getName() + " in slot " + (slot + 1));
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "Slot must be a number.");
            }
        } else {
            targetPlayer.getInventory().addItem(item);
            sender.sendMessage(ChatColor.YELLOW + "Given " + amount + " " + itemName + " to " + targetPlayer.getName());
            debugMode.info("Given " + amount + " " + itemName + " to " + targetPlayer.getName());
        }
    }

    @Override
    public List<String> tabComplete(Player player, String[] args) {
        List<String> suggestions = new ArrayList<>();
        if (args.length == 1) {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                suggestions.add(onlinePlayer.getName());
            }
        } else if (args.length == 2) {
            suggestions.addAll(items.keySet());
        }
        return suggestions;
    }
}
