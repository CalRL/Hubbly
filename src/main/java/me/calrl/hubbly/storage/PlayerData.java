package me.calrl.hubbly.storage;

import me.calrl.hubbly.enums.PluginKeys;
import me.calrl.hubbly.enums.data.PlayerMovementMode;
import me.calrl.hubbly.enums.data.PlayerVisibilityMode;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

public final class PlayerData {

    private final UUID uuid;
    private final String name;

    private final PlayerMovementData movement;
    private final PlayerVisibilityData visibility;

    public PlayerData(
            UUID uuid,
            String name,
            PlayerMovementData movement,
            PlayerVisibilityData visibility
    ) {
        this.uuid = uuid;
        this.name = name;
        this.movement = movement;
        this.visibility = visibility;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public PlayerMovementData movement() {
        return movement;
    }

    public PlayerVisibilityData visibility() {
        return visibility;
    }

    public static PlayerData from(Player player) {
        UUID uuid = player.getUniqueId();
        String name = player.getName();

        PersistentDataContainer container = player.getPersistentDataContainer();
        PlayerMovementMode movementMode = PlayerMovementMode.NONE;
        if (container.has(PluginKeys.MOVEMENT_KEY.getKey(), PersistentDataType.STRING)) {
            String movementString = container.get(PluginKeys.MOVEMENT_KEY.getKey(), PersistentDataType.STRING);
            if (movementString != null) {
                movementMode = PlayerMovementMode.valueOf(movementString);
            }
        }

        PlayerVisibilityMode visibilityMode = PlayerVisibilityMode.VISIBLE;
        if (container.has(PluginKeys.PLAYER_VISIBILITY.getKey(), PersistentDataType.STRING)) {
            String visibilityString = container.get(PluginKeys.PLAYER_VISIBILITY.getKey(), PersistentDataType.STRING);
            if (visibilityString != null) {
                visibilityMode = PlayerVisibilityMode.valueOf(visibilityString);
            }
        }

        PlayerMovementData movementData = new PlayerMovementData(movementMode);
        PlayerVisibilityData visibilityData = new PlayerVisibilityData(visibilityMode);

        return new PlayerData(uuid, name, movementData, visibilityData);
    }
}