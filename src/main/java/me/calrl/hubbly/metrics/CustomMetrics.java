package me.calrl.hubbly.metrics;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.handlers.PlayerMovementHandler;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class CustomMetrics {
    private final Hubbly plugin;
    private final Metrics metrics;
    public CustomMetrics(Hubbly plugin) {
        this.plugin = plugin;
        final int pluginId = 22219;
        this.metrics = new Metrics(plugin, pluginId);
        this.addCharts();
    }

    public void addCharts() {
        this.metrics.addCustomChart(this.databaseChart());
        this.metrics.addCustomChart(this.movementChart());
        this.featuresChart(this.metrics);
    }

    private Metrics.SimplePie databaseChart() {
        return new Metrics.SimplePie("db_enabled", () -> {
            if(plugin.getStorageManager() == null) {
                return Boolean.FALSE.toString();
            }

            return String.valueOf(plugin.getStorageManager().isActive());
        });
    }

    private Metrics.AdvancedPie movementChart() {
        return new Metrics.AdvancedPie("movement_chart", () -> {
            Map<String, Integer> map = new HashMap<>();
            map.put("fly", 0);
            map.put("none", 0);
            map.put("doublejump", 0);
            for(Player player : plugin.getServer().getOnlinePlayers()) {
                PlayerMovementHandler handler = new PlayerMovementHandler(player, plugin);
                String mode = handler.getMovementMode().toString().toLowerCase();
                Integer num = map.get(mode);
                map.replace(mode, num + 1);
            }

            return map;
        });
    }

    private void featuresChart(Metrics metrics) {
        metrics.addCustomChart(new Metrics.SimplePie("launchpad", () ->
                plugin.getConfig().getBoolean("launchpad.enabled") ? "Enabled" : "Disabled"
        ));

        metrics.addCustomChart(new Metrics.SimplePie("doublejump", () ->
                plugin.getConfig().getBoolean("double_jump.enabled") ? "Enabled" : "Disabled"
        ));

        metrics.addCustomChart(new Metrics.SimplePie("antivoid", () ->
                plugin.getConfig().getBoolean("antivoid.enabled") ? "Enabled" : "Disabled"
        ));

        metrics.addCustomChart(new Metrics.SimplePie("experience", () ->
                plugin.getConfig().getBoolean("player.experience.enabled") ? "Enabled" : "Disabled"
        ));

        metrics.addCustomChart(new Metrics.SimplePie("announcements", () ->
                plugin.getConfig().getBoolean("announcements.enabled") ? "Enabled" : "Disabled"
        ));
    }
}
