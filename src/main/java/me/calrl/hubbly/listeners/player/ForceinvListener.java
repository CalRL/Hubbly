package me.calrl.hubbly.listeners.player;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.enums.Permissions;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class ForceinvListener implements Listener {

    private Hubbly plugin;
    public ForceinvListener(Hubbly plugin) {
        this.plugin = plugin;
    }
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if(plugin.getDisabledWorldsManager().inDisabledWorld(player.getLocation())) { return; }
        boolean forceInventory = plugin.getConfig().getBoolean("player.forceinventory");


        if (forceInventory && !player.hasPermission(Permissions.BYPASS_FORCE_INVENTORY.getPermission())) {
            event.setCancelled(true);
            player.setItemOnCursor(new ItemStack(Material.AIR));
        }
    }

}
