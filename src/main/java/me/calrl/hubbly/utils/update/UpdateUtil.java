package me.calrl.hubbly.utils.update;

import me.calrl.hubbly.Hubbly;
import org.bukkit.configuration.ConfigurationSection;

import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

public class UpdateUtil {

    private String updateMessage = null;
    private boolean needsUpdate;
    public boolean checkForUpdate(Hubbly plugin) {
        Logger logger = plugin.getLogger();

        CompletableFuture<Boolean> updateFuture = new CompletableFuture<>();

        UpdateChecker.init(plugin, 117243).requestUpdateCheck().whenComplete((result, exception) -> {
            UpdateChecker.UpdateReason reason = result.getReason();
            ConfigurationSection section = plugin.getConfig().getConfigurationSection("update");

            if(section == null) {
                logger.warning("Please report this to the developer...");
                logger.warning("'update' is NULL in config... ");
                updateFuture.complete(false);
                return;
            }

            switch(reason) {
                case UpdateChecker.UpdateReason.UP_TO_DATE -> {
                    needsUpdate = false;
                    updateMessage = parsePlaceholders(section.getString("no_update", "No update"), result);
                    logger.info(updateMessage);
                    }
                case UpdateChecker.UpdateReason.NEW_UPDATE -> {
                    needsUpdate = true;
                    updateMessage = parsePlaceholders(section.getString("new_update", "A new update for Hubbly is available"), result);

                    logger.info(updateMessage);

                }
                case UpdateChecker.UpdateReason.UNRELEASED_VERSION -> {
                    needsUpdate = false;
                    updateMessage = String.format("You're running a development build (%s)...", plugin.getDescription().getVersion());

                    logger.info(updateMessage);
                    logger.info("Proceed with caution.");
                }
                default -> {
                    needsUpdate = false;
                    updateMessage = parsePlaceholders(section.getString("error", "Could not check for a new version..."), result);

                    logger.info(updateMessage);
                    logger.info("Reason: " + reason);
                }

            }
            updateFuture.complete(needsUpdate);
        });

        try {
            return updateFuture.get();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public String parsePlaceholders(String message, UpdateChecker.UpdateResult result) {
        if(message.contains("%current%")) {
            message = message.replace("%current%", Hubbly.getInstance().getDescription().getVersion());
        }
        if(message.contains("%new%")) {
            final String newest = result.getNewestVersion();
            message = message.replace("%new%", newest);

        }
        if(message.contains("%link%")) {
            String link = "https://www.spigotmc.org/resources/hubbly-1-20-6-1-21-4-the-only-hub-plugin-you-will-ever-need.117243/";
            message = message.replace("%link%", link);
        }

        return message;
    }
    public boolean getNeedsUpdate() {
        return needsUpdate;
    }


    public String getMessage() {
        return this.updateMessage;
    }
}
