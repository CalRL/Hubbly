package me.calrl.hubbly.utils;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.enums.PluginKeys;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ItemBuilder {
    private ItemStack itemStack;
    private ItemMeta itemMeta;
    private Player player;

    public ItemBuilder() {

    }

    public ItemBuilder(Material material) {
        this.itemStack = new ItemStack(material);
        this.itemMeta = itemStack.getItemMeta();
    }
    public ItemBuilder(Material material, String displayName) {
        this.itemStack = new ItemStack(material);
        this.itemMeta = itemStack.getItemMeta();
        this.setName(displayName);
    }
    public ItemBuilder(Material material, String displayName, int modelData) {
        this.itemStack = new ItemStack(material);
        this.itemMeta = itemStack.getItemMeta();
        this.setName(displayName);
        this.setCustomModelData(modelData);
    }

    /**
     *
     * @param player
     * @return
     */
    public ItemBuilder setPlayer(Player player) {
        this.player = player;
        return this;
    }

    /**
     * Sets the item display name
     * @param name
     * @return
     */
    public ItemBuilder setName(String name) {
        if (itemMeta != null && name != null) {
            name = ChatUtils.processMessage(player, name);
            itemMeta.setDisplayName(name);
        }
        return this;
    }

    /**
     * Sets the item lore
     * @param lore
     * @return
     */
    public ItemBuilder setLore(List<String> lore) {
        if (itemMeta != null && lore != null) {
            lore = lore.stream().map(line -> ChatUtils.processMessage(player, line)).collect(Collectors.toList());

            itemMeta.setLore(lore);
        }
        return this;
    }
    public ItemBuilder setCustomModelData(Integer modelData) {
        if (itemMeta != null && modelData != null) {
            itemMeta.setCustomModelData(modelData);
        }
        return this;
    }
    public ItemBuilder addPersistentData(NamespacedKey key, PersistentDataType<String, String> type, String value) {
        if (itemMeta != null && key != null && value != null) {
            PersistentDataContainer container = itemMeta.getPersistentDataContainer();
            container.set(key, type, value);
        }
        return this;
    }

    public ItemBuilder addEnchant(Enchantment enchantment, int level) {
        if(itemMeta != null && enchantment != null && level > 0) {
            itemMeta.addEnchant(enchantment, level, true);
        }
        return this;
    }

    public ItemBuilder addGlow() {
        if(itemMeta != null) {
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            itemMeta.addEnchant(Enchantment.THORNS, 1, true);
        }
        return this;
    }

    public ItemBuilder setTextures(String texture) {
        if(itemMeta != null && this.itemStack.getType() == Material.PLAYER_HEAD) {
            SkullMeta skullMeta = (SkullMeta) itemMeta;
                PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID());
                PlayerTextures textures = profile.getTextures();

                if(texture.startsWith("http")) {
                    try {
                        URI uri = new URI(texture);
                        URL url = uri.toURL();
                        textures.setSkin(url);


                    } catch (MalformedURLException e) {
                        e.printStackTrace();

                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);

                    }
                } else {
                    Hubbly.getInstance().getDebugMode().info("Texture value: " + texture + " is malformed. Please add 'https://' in front of the URL if not there already.");
                    Hubbly.getInstance().getDebugMode().info("If that does not fix the issue, please get support in the discord");
                }

                profile.setTextures(textures);

                skullMeta.setOwnerProfile(profile);
        }
        return this;
    }

    public ItemBuilder setColor(Color color) {
        Material type = itemStack.getType();
        if(type == Material.LEATHER_BOOTS || type == Material.LEATHER_CHESTPLATE || type == Material.LEATHER_LEGGINGS || type == Material.LEATHER_HELMET) {
            LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta) itemMeta;
            leatherArmorMeta.setColor(color);
        } else {
            throw new IllegalArgumentException("Color is only applicable to leather armor!");
        }
        return this;
    }

    public ItemBuilder setAmount(int amount) {
        itemStack.setAmount(amount);
        return this;
    }

    public ItemBuilder setSkullOwner() {
        if(itemMeta != null && itemStack.getType() == Material.PLAYER_HEAD) {
            SkullMeta skullMeta = (SkullMeta) itemMeta;
            skullMeta.setOwnerProfile(player.getPlayerProfile());
        }
        return this;
    }

    public ItemBuilder addFlags(List<String> flags) {
        List<ItemFlag> itemFlags = new ArrayList<>();
        for(String flag : flags) {
            try {
                ItemFlag itemFlag = ItemFlag.valueOf(flag);
                itemFlags.add(itemFlag);
            } catch (IllegalArgumentException ignored) {}
        }
        itemMeta.addItemFlags(itemFlags.toArray(new ItemFlag[0]));
        return this;
    }

    /**
     * TODO: flip these two methods this is ugly asl
     * @param section
     * @return
     */
    public ItemStack fromConfig(ConfigurationSection section) {
        if(player == null) {
            return null;
        }
        return fromConfig(this.player, section);
    }

    public ItemStack fromConfig(Player player, ConfigurationSection section) {

        String materialValue = section.getString("material").toUpperCase();
        Material material = Material.getMaterial(materialValue);

        ItemBuilder builder = new ItemBuilder(material);
        builder.setPlayer(player);

        if(section.contains("amount")) {
            builder.setAmount(section.getInt("amount"));
        }

        if(section.contains("url")) {
            builder.setTextures(section.getString("url"));
        }
        /*
         * This is bad... urls should be put in the url section,
         * not here, however i will keep this here for the first few versions
         * TODO: remove this soon
         */
        if(section.contains("texture")) {
            String texture = section.getString("texture", "player");
            if(texture.equalsIgnoreCase("player")) {
                builder.setSkullOwner();
            } else {
                builder.setTextures(texture);
            }
        }
        if(section.contains("base64")) {

        }

        if(section.contains("hdb")) {

        }

        if(section.contains("lore")) {
            builder.setLore(section.getStringList("lore"));
        }

        if(section.contains("name")) {
            builder.setName(section.getString("name"));
        }

        if(section.contains("actions")) {
            List<String> actions = section.getStringList("actions");
            String actionData = String.join(",", actions);
            builder.addPersistentData(PluginKeys.ACTIONS_KEY.getKey(), PersistentDataType.STRING, actionData);
        }

        if(section.contains("color")) {
            builder.setColor(section.getColor("color"));
        }

        if(section.contains("glow") && section.getBoolean("glow")) {
            builder.addGlow();
        }

        if(section.contains("custom_model_data")) {
            builder.setCustomModelData(section.getInt("custom_model_data"));
        }

        if(section.contains("item_flags")) {
            List<String> flags = section.getStringList("item_flags");
        }

        return builder.build();
    }

    /**
     * Builds the item and returns the final ItemStack
     */
    public void saveTo() {}


    public ItemStack build() {
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }



}
