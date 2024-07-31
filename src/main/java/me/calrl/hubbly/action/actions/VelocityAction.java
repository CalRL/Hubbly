package me.calrl.hubbly.action.actions;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.action.Action;
import org.bukkit.entity.Player;

public class VelocityAction implements Action {

    @Override
    public String getIdentifier() {
        return "VELOCITY";
    }

    @Override
    public void execute(Hubbly plugin, Player player, String data) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("connect");
        out.writeUTF(player.getName());
        out.writeUTF(data);
        player.sendPluginMessage(plugin, "velocity:player", out.toByteArray());
    }
}