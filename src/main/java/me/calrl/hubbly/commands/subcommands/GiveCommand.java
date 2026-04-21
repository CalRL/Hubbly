package me.calrl.hubbly.commands.subcommands;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.action.Action;
import me.calrl.hubbly.action.ActionManager;
import me.calrl.hubbly.enums.Permissions;
import me.calrl.hubbly.enums.Result;
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
        this.items = plugin.gameplay().itemsManager().getItems();
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
            builder.setKey("subcommands.give.usage").send();
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
        ItemsManager itemsManager = this.plugin.gameplay().itemsManager();
        CustomItem customItem = itemsManager.getItems().get(itemName);

        if (customItem == null) {
            builder
                    .setKey("unknown_item")
                    .replace("%value%", itemName)
                    .send();
            return;
        }
        customItem.setPlayer(targetPlayer);
        ItemStack item = customItem.createItem();

        Integer amount = 1;
        Integer slot = null;

        // Parse the AMOUNT field
        if(arguments.size() >= 3) {
            String amountString = arguments.get(2);
            Integer parsed = parseInt(amountString);

            if (parsed == null) {
                builder.setKey("arg_must_be_number")
                        .replace("%value%", amountString)
                        .send();
                return;
            }

            if (parsed < 1 || parsed > item.getMaxStackSize()) {
                builder.setKey("invalid_amount")
                        .replace("%value%", amountString)
                        .send();
                return;
            }

            amount = parsed;
        }

        // Parse the SLOT field
        if (arguments.size() >= 4) {
            String slotString = arguments.get(3);
            Integer parsed = parseInt(slotString);

            if (parsed == null) {
                builder.setKey("arg_must_be_number")
                        .replace("%value%", slotString)
                        .send();
                return;
            }

            slot = parsed - 1;

            if (slot < 0 || slot >= targetPlayer.getInventory().getSize()) {
                builder.setKey("invalid_argument")
                        .replace("%value%", slotString)
                        .send();
                return;
            }
        }

        this.give(targetPlayer, item, amount, slot);

        // Message handling
        if (slot != null) {
            builder.setKey("subcommands.give.messages.give_item_with_slot")
                    .replace("%amount%", String.valueOf(amount))
                    .replace("%item%", itemName)
                    .replace("%player%", targetPlayer.getName())
                    .replace("%slot%", String.valueOf(slot + 1))
                    .send();
        } else {
            builder.setKey("subcommands.give.messages.give_item")
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

    private Integer parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private void give(Player player, ItemStack item) {
        this.give(player, item, 1, null);
    }

    private void give(Player player, ItemStack item, int amount) {
        this.give(player, item, amount, null);
    }

    private void give(Player player, ItemStack item, int amount, Integer slot) {
        item.setAmount(amount);

        if (slot != null) {
            player.getInventory().setItem(slot, item);
        } else {
            player.getInventory().addItem(item);
        }
    }
}
