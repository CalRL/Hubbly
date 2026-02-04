package me.calrl.hubbly.action.actions;

import me.calrl.hubbly.PluginTestBase;
import me.calrl.hubbly.managers.FileManager;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ActionTests extends PluginTestBase {

    @Test
    void messageAction_sendsMessageToPlayer() {
        PlayerMock player = server.addPlayer("TestUser");
        MessageAction action = new MessageAction();

        action.execute(plugin, player, "Hello there");

        String message = player.nextMessage();
        assertNotNull(message);
        assertTrue(message.toLowerCase().contains("hello"));
    }

    @Test
    void gamemodeAction_setsPlayerGamemode() {
        PlayerMock player = server.addPlayer();
        GamemodeAction action = new GamemodeAction();

        action.execute(plugin, player, "creative");

        assertEquals(GameMode.CREATIVE, player.getGameMode());
    }

    @Test
    void slotAction_setsHeldItemSlotUsingOneBasedIndex() {
        PlayerMock player = server.addPlayer();
        SlotAction action = new SlotAction();

        action.execute(plugin, player, "2");

        assertEquals(1, player.getInventory().getHeldItemSlot());
    }

    @Test
    void clearAction_clearsInventoryContents() {
        PlayerMock player = server.addPlayer();
        ClearAction action = new ClearAction();
        PlayerInventory inventory = player.getInventory();
        inventory.setItem(0, new ItemStack(Material.STONE));
        inventory.setItem(1, new ItemStack(Material.DIAMOND));

        action.execute(plugin, player, "");

        assertTrue(Arrays.stream(inventory.getContents())
                .allMatch(item -> item == null || item.getType() == Material.AIR));
    }

    @Test
    void effectAction_appliesPotionEffect() {
        PlayerMock player = server.addPlayer();
        EffectAction action = new EffectAction();

        action.execute(plugin, player, "SWIFTNESS;200;1");

        PotionEffect effect = player.getPotionEffect(PotionEffectType.SPEED);
        assertNotNull(effect);
        assertEquals(200, effect.getDuration());
        assertEquals(1, effect.getAmplifier());
    }

    @Test
    void launchAction_setsVelocityFromConfigPower() {
        PlayerMock player = server.addPlayer();
        LaunchAction action = new LaunchAction();

        player.setRotation(0F, 0F);
        action.execute(plugin, player, "2.0;0.5");
        server.getScheduler().performTicks(1);

        Vector velocity = player.getVelocity();
        assertEquals(1.0, velocity.getY(), 0.0001);
        assertEquals(2.0, velocity.getZ(), 0.0001);
    }

    @Test
    void menuAction_opensConfiguredMenu() {
        PlayerMock player = server.addPlayer();
        MenuAction action = new MenuAction();

        FileManager manager = plugin.getFileManager();
        YamlConfiguration config = manager.getConfig("menus/selector.yml");

        action.execute(plugin, player, "selector");
        server.getScheduler().performTicks(1);

        List<Material> materials = new ArrayList<>();
        ItemStack[] items = player.getOpenInventory().getTopInventory().getContents();
        for(ItemStack itemStack : items) {
            Material type = itemStack.getType();
            if(materials.contains(type)) continue;
            materials.add(type);
        }

        assertTrue(materials.contains(Material.BLACK_STAINED_GLASS_PANE));
        assertTrue(materials.contains(Material.LEATHER_CHESTPLATE));

        assertNotNull(player.getOpenInventory());
        assertEquals(36, player.getOpenInventory().getTopInventory().getSize());
    }
}
