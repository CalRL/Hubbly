package me.calrl.hubbly.action.actions;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.action.Action;
import me.calrl.hubbly.managers.DebugMode;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

public class FireworkAction implements Action {
    @Override
    public String getIdentifier() {
        return "FIREWORK";
    }

    @Override
    public void execute(Hubbly plugin, Player player, String data) {
        DebugMode debugMode = plugin.getDebugMode();
        String[] args = data.split(";");
        try {
            FireworkEffect.Builder builder = FireworkEffect.builder().with(FireworkEffect.Type.valueOf(args[0])).withColor(Color.fromRGB(Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]))).withTrail();

            Firework firework = player.getWorld().spawn(player.getLocation(), Firework.class);
            FireworkMeta meta = firework.getFireworkMeta();
            meta.addEffect(builder.build());
            meta.setPower(Integer.parseInt(args[4]));
            firework.setFireworkMeta(meta);
        } catch (Exception e) {
            debugMode.severe("Firework action failed, printing stacktrace...");
            e.printStackTrace();
        }


    }
}
