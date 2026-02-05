package me.calrl.hubbly.utils;

import me.calrl.hubbly.PluginTestBase;
import me.calrl.hubbly.enums.PluginKeys;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.persistence.PersistentDataType;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;


import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ItemBuilderTests extends PluginTestBase {
    @Test
    void constructor_setsMaterial() {
        ItemBuilder builder = new ItemBuilder(Material.DIAMOND);
        ItemStack item = builder.build();

        assertEquals(Material.DIAMOND, item.getType());
    }

    @Test
    void setName_setsDisplayName() {
        PlayerMock player = server.addPlayer();

        ItemStack item = new ItemBuilder(Material.STONE)
                .setPlayer(player)
                .setName("Hello")
                .build();

        assertEquals("Hello", item.getItemMeta().getDisplayName());
    }

    @Test
    void setLore_setsLoreLines() {
        PlayerMock player = server.addPlayer();

        ItemStack item = new ItemBuilder(Material.STONE)
                .setPlayer(player)
                .setLore(List.of("Line 1", "Line 2"))
                .build();

        assertEquals(List.of("Line 1", "Line 2"), item.getItemMeta().getLore());
    }

    @Test
    void setCustomModelData_setsModelData() {
        ItemStack item = new ItemBuilder(Material.STONE)
                .setCustomModelData(123)
                .build();

        assertEquals(123, item.getItemMeta().getCustomModelData());
    }

    @Test
    void addPersistentData_writesToPdc() {
        ItemStack item = new ItemBuilder(Material.STONE)
                .addPersistentData(
                        PluginKeys.ACTIONS_KEY.getKey(),
                        PersistentDataType.STRING,
                        "test"
                )
                .build();

        String stored = item.getItemMeta()
                .getPersistentDataContainer()
                .get(PluginKeys.ACTIONS_KEY.getKey(), PersistentDataType.STRING);

        assertEquals("test", stored);
    }

    @Test
    void addEnchant_addsEnchant() {
        ItemStack item = new ItemBuilder(Material.DIAMOND_SWORD)
                .addEnchant(Enchantment.SHARPNESS, 3)
                .build();

        assertEquals(3, item.getItemMeta().getEnchantLevel(Enchantment.SHARPNESS));
    }

    @Test
    void addGlow_addsHiddenEnchant() {
        ItemStack item = new ItemBuilder(Material.STONE)
                .addGlow()
                .build();

        ItemMeta meta = item.getItemMeta();

        assertTrue(meta.hasEnchant(Enchantment.THORNS));
        assertTrue(meta.getItemFlags().contains(ItemFlag.HIDE_ENCHANTS));
    }

    @Test
    void setUnbreakable_setsFlag() {
        ItemStack item = new ItemBuilder(Material.STONE)
                .setUnbreakable()
                .build();

        assertTrue(item.getItemMeta().isUnbreakable());
    }

    @Test
    void setAmount_setsStackSize() {
        ItemStack item = new ItemBuilder(Material.STONE)
                .setAmount(5)
                .build();

        assertEquals(5, item.getAmount());
    }

    @Test
    void setColor_appliesToLeatherArmor() {
        ItemStack item = new ItemBuilder(Material.LEATHER_HELMET)
                .setColor(Color.RED)
                .build();

        LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
        assertEquals(Color.RED, meta.getColor());
    }

    @Test
    void setColor_throwsOnNonLeather() {
        ItemBuilder builder = new ItemBuilder(Material.DIAMOND_HELMET);

        assertThrows(IllegalArgumentException.class, () ->
                builder.setColor(Color.RED)
        );
    }

    @Test
    void fromConfig_buildsBasicItem() {
        PlayerMock player = server.addPlayer();

        YamlConfiguration config = new YamlConfiguration();
        config.set("material", "STONE");
        config.set("name", "Test Item");
        config.set("lore", List.of("A", "B"));
        config.set("amount", 2);
        config.set("model_data", 42);
        config.set("glow", true);

        ItemStack item = new ItemBuilder()
                .fromConfig(player, config)
                .build();

        ItemMeta meta = item.getItemMeta();

        assertEquals(Material.STONE, item.getType());
        assertEquals("Test Item", meta.getDisplayName());
        assertEquals(List.of("A", "B"), meta.getLore());
        assertEquals(2, item.getAmount());
        assertEquals(42, meta.getCustomModelData());
        assertTrue(meta.hasEnchant(Enchantment.THORNS));
    }
}
