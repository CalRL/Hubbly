package me.calrl.hubbly.listeners.world;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.WorldMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.managers.DisabledWorlds;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissionAttachment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class WorldEventListenersTest {

    private ServerMock server;
    private Hubbly plugin;
    private WorldEventListeners listener;
    private PlayerMock player;
    private WorldMock world;
    private WorldMock disabledWorld;

    @BeforeEach
    public void setUp() {
        server = MockBukkit.getOrCreateMock();
        plugin = MockBukkit.load(Hubbly.class);
        listener = new WorldEventListeners(plugin);

        // Create worlds
        world = server.addSimpleWorld("world");
        disabledWorld = server.addSimpleWorld("disabled_world");

        // Setup player in regular world
        player = server.addPlayer();
        player.teleport(new Location(world, 0, 0, 0));

        // Register listener
        server.getPluginManager().registerEvents(listener, plugin);

        // Mock disabled worlds manager
        DisabledWorlds disabledWorlds = plugin.getDisabledWorldsManager();
        World disabledWorld = server.getWorld("disabled_world");
        disabledWorlds.addWorld(disabledWorld);
    }

    @Test
    public void testOnBlockPlace_CancelledWhenInDisabledWorld() {
        player.teleport(new Location(disabledWorld, 0, 0, 0));

        // Create a proper block state for the replaced block
        Block replacedBlock = disabledWorld.getBlockAt(0, 0, 0);
        BlockState replacedBlockState = replacedBlock.getState();

        Block placedAgainst = disabledWorld.getBlockAt(0, 1, 0);
        boolean canBuild = true;

        BlockPlaceEvent event = new BlockPlaceEvent(
                replacedBlock,                    // The block that was placed
                replacedBlockState,               // The state of the block being replaced
                placedAgainst,                    // The block against which it was placed
                new ItemStack(Material.STONE),   // The item being placed
                player,                           // The player placing it
                canBuild                          // Whether the player can build
        );

        server.getPluginManager().callEvent(event);
        assertFalse(event.isCancelled());
    }

    @Test
    public void testOnBlockPlace_BypassPermission() {
        player.addAttachment(plugin, "hubbly.bypass.place", true);
        BlockPlaceEvent event = new BlockPlaceEvent(
                world.getBlockAt(0, 0, 0),
                null,
                null,
                new ItemStack(Material.STONE),
                player,
                true
        );

        server.getPluginManager().callEvent(event);
        assertFalse(event.isCancelled());
    }

    @Test
    public void testOnBlockPlace_CancelledWhenConfigured() {
        plugin.getConfig().set("cancel_events.block_place", true);
        BlockPlaceEvent event = new BlockPlaceEvent(
                world.getBlockAt(0, 0, 0),
                null,
                null,
                new ItemStack(Material.STONE),
                player,
                true
        );

        server.getPluginManager().callEvent(event);
        assertTrue(event.isCancelled());
    }

    @Test
    public void testOnBlockBreak_CancelledWhenInDisabledWorld() {
        player.teleport(new Location(disabledWorld, 0, 0, 0));

        BlockBreakEvent event = new BlockBreakEvent(
                world.getBlockAt(0, 0, 0),
                player
        );

        server.getPluginManager().callEvent(event);
        assertFalse(event.isCancelled());
    }

    @Test
    public void testOnBlockBreak_BypassPermission() {
        PermissionAttachment attachment = player.addAttachment(plugin, "hubbly.bypass.break", true);
        BlockBreakEvent event = new BlockBreakEvent(
                world.getBlockAt(0, 0, 0),
                player
        );

        server.getPluginManager().callEvent(event);
        assertFalse(event.isCancelled());

        player.removeAttachment(attachment);
    }

    @Test
    public void testOnBlockBreak_CancelledWhenConfigured() {
        plugin.getConfig().set("cancel_events.block_break", true);
        BlockBreakEvent event = new BlockBreakEvent(
                world.getBlockAt(0, 0, 0),
                player
        );

        server.getPluginManager().callEvent(event);
        assertTrue(event.isCancelled());
    }

    @Test
    public void testCancelDamage_CancelledWhenInDisabledWorld() {
        player.teleport(new Location(disabledWorld, 0, 0, 0));

        EntityDamageEvent event = new EntityDamageEvent(
                player,
                EntityDamageEvent.DamageCause.FALL,
                10.0
        );

        server.getPluginManager().callEvent(event);
        assertFalse(event.isCancelled());
    }

    @Test
    public void testCancelDamage_BypassPermission() {
        player.addAttachment(plugin, "hubbly.bypass.damage", true);
        EntityDamageEvent event = new EntityDamageEvent(
                player,
                EntityDamageEvent.DamageCause.FALL,
                10.0
        );

        server.getPluginManager().callEvent(event);
        assertFalse(event.isCancelled());
    }

    @Test
    public void testCancelDamage_CancelledWhenConfigured() {
        plugin.getConfig().set("cancel_events.damage", true);

        EntityDamageEvent event = new EntityDamageEvent(
                player,
                EntityDamageEvent.DamageCause.FALL,
                10.0
        );


        server.getPluginManager().callEvent(event);
        assertTrue(event.isCancelled());
    }
}
