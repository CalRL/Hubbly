package me.calrl.hubbly.events;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class HubblySpawnEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS_LIST = new HandlerList();
    private Player player;
    private Location location;
    private boolean isCancelled;
    
    public HubblySpawnEvent(Player player, Location spawnLocation) {
        this.player = player;
        this.isCancelled = false;
        this.location = spawnLocation;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        isCancelled = b;
    }
    public Player getPlayer() {
        return this.player;
    }
    public Location getLocation() {
        return this.location;
    }

}
