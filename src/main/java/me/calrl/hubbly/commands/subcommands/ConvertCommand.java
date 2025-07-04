package me.calrl.hubbly.commands.subcommands;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.enums.Permissions;
import me.calrl.hubbly.enums.PluginKeys;
import me.calrl.hubbly.interfaces.SubCommand;
import me.calrl.hubbly.inventory.InventoryBuilder;
import me.calrl.hubbly.utils.ItemBuilder;
import me.calrl.hubbly.utils.MessageBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import java.io.File;
import java.io.IOException;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

public class ConvertCommand implements SubCommand {

    private Hubbly plugin;
    public ConvertCommand(Hubbly plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "convert";
    }

    @Override
    public String getDescription() {
        return "Converts files";
    }

    @Override
    public String getUsage() {
        return "/hubbly convert <selector/socials>";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(sender.hasPermission(Permissions.COMMAND_CONVERT.getPermission())) {

            if(args.length != 2) {
                new MessageBuilder(plugin).setPlayer(sender).setKey("subcommands.convert.usage").send();
                return;
            }
            String name = args[1];
            if(!name.equals("selector") && !name.equals("socials")) {
                new MessageBuilder(plugin).setPlayer(sender).setKey("invalid_argument").replace("%value%", name).send();
                return;
            }

            sender.sendMessage("Converting " + name);

            if(name.equals("selector")) {
                saveInventoryToFile(
                        new InventoryBuilder().fromLegacySelector(
                                plugin.getFileManager().getConfig("serverselector.yml")),
                        "serverselector"
                );

            } else if(name.equals("socials")) {
                saveInventoryToFile(
                        new InventoryBuilder().fromLegacySocials(
                                        plugin.getConfig()),
                        "socials"
                );
            }
        }
    }

    @Deprecated(since = "3.0.0")
    private void saveInventoryToFile(InventoryBuilder builder, String fileName) {
        // Ensure the menus directory exists
        File menusDir = new File(Hubbly.getInstance().getDataFolder(), "menus");
        if (!menusDir.exists()) {
            menusDir.mkdirs();
        }

        // Create the file in the menus directory
        File file = new File(menusDir, fileName + ".yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        config.set("title", builder.getTitle());
        config.set("size", builder.getSize());
        config.set("permission", "hubbly.menu." + fileName);

        Map<ItemStack, Integer> itemCountMap = new HashMap<>();
        Map<ItemStack, List<Integer>> itemSlotMap = new HashMap<>();

        // Count item occurrences and their slots
        for (Map.Entry<Integer, ItemStack> entry : builder.getIcons().entrySet()) {
            ItemStack item = entry.getValue();
            int slot = entry.getKey();

            // Normalize item by removing slot-specific data
            ItemStack normalizedItem = item.clone();
            normalizedItem.setAmount(1);

            itemCountMap.put(normalizedItem, itemCountMap.getOrDefault(normalizedItem, 0) + 1);
            itemSlotMap.computeIfAbsent(normalizedItem, k -> new ArrayList<>()).add(slot);
        }

        int index = 0;
        for (Map.Entry<ItemStack, Integer> entry : itemCountMap.entrySet()) {
            ItemStack item = entry.getKey();
            int count = entry.getValue();
            List<Integer> slots = itemSlotMap.get(item);

            String path = "items." + index;

            // If the item appears more than 5 times, use slot -1
            if (count > 5) {
                config.set(path + ".slot", -1);
            } else {
                // Save individual slots if count is 5 or less
                for (int slot : slots) {
                    String slotPath = "items." + slot;
                    saveItemToConfig(config, item, slotPath, slot);
                }
                continue;
            }

            saveItemToConfig(config, item, path, -1);
            index++;
        }

        try {
            config.save(file);
            Hubbly.getInstance().getLogger().info("Inventory saved to " + file.getPath());
        } catch (IOException e) {
            Hubbly.getInstance().getLogger().severe("Failed to save inventory to " + fileName);
            e.printStackTrace();
        }
    }

    @Deprecated(since = "3.0.0")
    private void saveItemToConfig(FileConfiguration config, ItemStack item, String path, int slot) {
        if (slot != -1) {
            config.set(path + ".slot", slot);
        }

        config.set(path + ".material", item.getType().name());

        if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
            config.set(path + ".name", item.getItemMeta().getDisplayName());
        }

        if (item.getAmount() > 1) {
            config.set(path + ".amount", item.getAmount());
        }

        if (item.getItemMeta().hasLore()) {
            config.set(path + ".lore", item.getItemMeta().getLore());
        }

        if (item.getItemMeta().hasCustomModelData()) {
            config.set(path + ".model_data", item.getItemMeta().getCustomModelData());
        }

        if (item.getType() == Material.PLAYER_HEAD) {
            SkullMeta skullMeta = (SkullMeta) item.getItemMeta();
            if (skullMeta.hasOwner()) {
                PlayerProfile profile = skullMeta.getOwnerProfile();
                if (profile != null && profile.getTextures().getSkin() != null) {
                    String texture = profile.getTextures().getSkin().toString();
                    config.set(path + ".texture", texture);
                }
            }
        }

        if (item.getItemMeta().getPersistentDataContainer().has(PluginKeys.ACTIONS_KEY.getKey(), PersistentDataType.STRING)) {
            String actionData = item.getItemMeta().getPersistentDataContainer().get(PluginKeys.ACTIONS_KEY.getKey(), PersistentDataType.STRING);
            List<String> actions = List.of(actionData.split(","));
            config.set(path + ".actions", actions);
        }

        if (item.getItemMeta().hasEnchants()) {
            config.set(path + ".glow", true);
        }

        if (!item.getItemMeta().getItemFlags().isEmpty()) {
            List<String> flags = item.getItemMeta().getItemFlags().stream()
                    .map(ItemFlag::name)
                    .collect(Collectors.toList());
            config.set(path + ".item_flags", flags);
        }
    }





}
