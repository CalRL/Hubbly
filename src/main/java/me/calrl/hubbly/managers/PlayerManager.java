package me.calrl.hubbly.managers;

import me.calrl.hubbly.Hubbly;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class PlayerManager {
    private Hubbly plugin;
    public PlayerManager(Hubbly plugin) {
        this.plugin = plugin;
    }

    /**
     * Allows the player to fly and adds the fly key (set to doublejump by default)
     * @param player the target
     */
    public void addFlight(Player player) {
        PersistentDataContainer container = player.getPersistentDataContainer();
        if(!container.has(plugin.FLY_KEY)) {
            player.setAllowFlight(true);
            container.set(plugin.FLY_KEY, PersistentDataType.BYTE, (byte) 0);
        }
    }

    /**
     * Disables the player's flight and removes the fly key
     * @param player the target
     */
    public void removeFlight(Player player) {
        PersistentDataContainer container = player.getPersistentDataContainer();
        if(container.has(plugin.FLY_KEY)) {
            player.setAllowFlight(false);
            player.setFlying(false);
            container.remove(plugin.FLY_KEY);
        }

    }

    /**
     * @param player the target
     * @param state either 0 or 1
     */
    public void setState(Player player, byte state) {
        PersistentDataContainer container = player.getPersistentDataContainer();

        player.setAllowFlight(true);
        container.set(plugin.FLY_KEY, PersistentDataType.BYTE, state);
    }

    public byte getPlayerState(Player player) {
        PersistentDataContainer container = player.getPersistentDataContainer();
        if(!container.has(plugin.FLY_KEY, PersistentDataType.BYTE)) return (byte) -1;
        byte value = container.get(plugin.FLY_KEY, PersistentDataType.BYTE);

        return value;

    }

}
