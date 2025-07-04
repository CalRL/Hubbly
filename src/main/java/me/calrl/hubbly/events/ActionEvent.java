package me.calrl.hubbly.events;

import me.calrl.hubbly.action.Action;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ActionEvent extends Event implements Cancellable {
    private static HandlerList HANDLERS_LIST = new HandlerList();
    private Player player;
    private Action action;
    private String actionData;
    private boolean isCancelled;
    public ActionEvent(Player player, Action action, String actionData) {
        this.action = action;
        this.player = player;
        this.actionData = actionData;
        isCancelled = false;
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

    public String getActionIdentifier() {
        return action.getIdentifier();
    }

    public String getActionData() {
        return actionData;
    }


}
