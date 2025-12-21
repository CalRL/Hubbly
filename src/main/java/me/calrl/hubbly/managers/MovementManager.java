package me.calrl.hubbly.managers;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.commands.movement.MovementCommand;
import me.calrl.hubbly.enums.NBTKeys;
import me.calrl.hubbly.enums.Result;

import java.util.HashMap;
import java.util.UUID;

public class MovementManager {

    private final Hubbly plugin;
    private HashMap<UUID, NBTKeys> map;
    public MovementManager(Hubbly plugin) {
        this.plugin = plugin;
    }

    public Result update(UUID uuid, NBTKeys state) {
        return Result.NOTHING_TO_DO;
    }

    // add to hashmap
    public void onJoin(UUID uuid) {}
    // remove from map
    public void onLeave(UUID uuid) {}

}
