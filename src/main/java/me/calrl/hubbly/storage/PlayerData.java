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
                try {
                    movementMode = PlayerMovementMode.valueOf(movementString);
                } catch (IllegalArgumentException ignored) {}
            }
        }

        PlayerVisibilityMode visibilityMode = PlayerVisibilityMode.VISIBLE;
        if (container.has(PluginKeys.PLAYER_VISIBILITY.getKey(), PersistentDataType.STRING)) {
            String visibilityString = container.get(PluginKeys.PLAYER_VISIBILITY.getKey(), PersistentDataType.STRING);
            if (visibilityString != null) {
                try {
                    visibilityMode = PlayerVisibilityMode.valueOf(visibilityString);
                } catch (IllegalArgumentException ignored) {}
            }
        }

        PlayerMovementData movementData = new PlayerMovementData(movementMode);
        PlayerVisibilityData visibilityData = new PlayerVisibilityData(visibilityMode);

        return new PlayerData(uuid, name, movementData, visibilityData);
    }

    public boolean isValid() {
        return uuid != null
                && name != null
                && movement != null
                && movement.getMode() != null
                && visibility != null
                && visibility.getMode() != null;
    }

    @Override
    public String toString() {
        return String.format("[UUID: %s, NAME: %s, MOVEMENT: %s, VISIBILITY: %s]", this.uuid, this.name, this.movement, this.visibility);
    }

    public void assertValid() {
        if (!isValid()) {
            throw new IllegalStateException("Invalid PlayerData: " + this);
        }
    }
}