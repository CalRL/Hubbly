package me.calrl.hubbly.action.actions;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.action.Action;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class LaunchAction implements Action {
    @Override
    public String getIdentifier() {
        return "LAUNCH";
    }

    @Override
    public void execute(Hubbly plugin, Player player, String data) {
        new BukkitRunnable() {

            private final double powerY = plugin.getConfig().getDouble("launchpad.power_y");
            private final double power = plugin.getConfig().getDouble("launchpad.power");
            @Override
            public void run() {
                Vector direction = player.getLocation().getDirection();

                direction.setY(powerY);

                direction.multiply(power);

                player.setVelocity(direction);
            }
        }.runTask(plugin);
    }
}
