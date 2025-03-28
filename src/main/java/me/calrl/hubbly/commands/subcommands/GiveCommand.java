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
import me.calrl.hubbly.utils.MessageBuilder;
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
        MessageBuilder builder = new MessageBuilder().setPlugin(plugin).setPlayer(sender);
        if (!sender.hasPermission(Permissions.COMMAND_GIVE.getPermission())) {
            builder.setKey("no_permission_command").send();
            return;
        }
        List<String> arguments = new ArrayList<>(Arrays.asList(args));
        arguments.removeIf(arg -> arg.equalsIgnoreCase("give"));

        // Argument check
        if (arguments.size() < 2) {
            builder.setMessage(this.getUsage()).send();
            return;
        }

        // Find the target player
        String playerName = arguments.getFirst();
        Player targetPlayer = Bukkit.getPlayer(playerName);
        if (targetPlayer == null) {
            builder.setKey("unknown_player").send();
            return;
        }

        // Find the item
        String itemArg = arguments.get(1);
        String itemName = ChatColor.stripColor(itemArg.toLowerCase());
        CustomItem customItem = items.get(itemName);
        if (customItem == null) {
            builder
                    .setKey("unknown_item")
                    .replace("%value%", itemName)
                    .send();
            return;
        }

        ItemStack item = customItem.createItem();
        int amount = 1;
        if (arguments.size() >= 3) {
            try {
                String amountString = arguments.get(2);
                amount = Integer.parseInt(amountString);
                if (amount < 1 || amount > item.getMaxStackSize()) {
                    builder
                            .setKey("invalid_amount")
                            .replace("%value%", amountString)
                            .send();
                    return;
                }
                item.setAmount(amount);
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "Amount must be a number.");
                new MessageBuilder(plugin)
                        .setKey("arg_must_be_number")
                        .replace("%value%", String.valueOf(amount))
                        .send();
                return;
            }
        }

        // If slot argument is given
        if (arguments.size() == 4) {
            String slotString = arguments.get(3);
            int slot = Integer.parseInt(slotString) - 1;
            try {
                if (slot < 0 || slot >= targetPlayer.getInventory().getSize()) {
                    new MessageBuilder(plugin)
                            .setPlayer(sender)
                            .setKey("invalid_argument")
                            .replace("%value%", String.valueOf(slot))
                            .send();

                    return;
                }
                targetPlayer.getInventory().setItem(slot, item);
                int finalSlot = slot + 1;
                String finalPlayerName = targetPlayer.getName();
//                sender.sendMessage(ChatColor.YELLOW + "Given " + amount + " " + itemName + " to " + finalPlayerName + " in slot " + finalSlot);
                new MessageBuilder(plugin)
                        .setPlayer(sender)
                        .setKey("subcommands.give.messages.give_item_with_slot")
                        .replace("%amount%", String.valueOf(amount))
                        .replace("%item%", itemName)
                        .replace("%player%", finalPlayerName)
                        .replace("%slot%", String.valueOf(finalSlot))
                        .send();
            } catch (NumberFormatException e) {
                new MessageBuilder(plugin)
                        .setKey("arg_must_be_number")
                        .replace("%value%", slotString)
                        .send();
            }
        } else {
            targetPlayer.getInventory().addItem(item);
//            sender.sendMessage(ChatColor.YELLOW + "Given " + amount + " " + itemName + " to " + targetPlayer.getName());
            new MessageBuilder(plugin)
                    .setPlayer(sender)
                    .setKey("subcommands.give.messages.give_item")
                    .replace("%amount%", String.valueOf(amount))
                    .replace("%item%", itemName)
                    .replace("%player%", targetPlayer.getName())
                    .send();
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
