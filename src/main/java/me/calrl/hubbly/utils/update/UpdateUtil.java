package me.calrl.hubbly.utils.update;

import me.calrl.hubbly.Hubbly;
import org.bukkit.configuration.ConfigurationSection;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

public class UpdateUtil {

    private String updateMessage;
    public boolean checkForUpdate(Hubbly plugin) {


        Logger logger = plugin.getLogger();

        CompletableFuture<Boolean> updateFuture = new CompletableFuture<>();

        UpdateChecker.init(plugin, 117243).requestUpdateCheck().whenComplete((result, exception) -> {
            boolean needsUpdate;
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
                    updateMessage = parseVersion(section.getString("no_update", "No update"), result);

                    logger.info(updateMessage);
                    }
                case UpdateChecker.UpdateReason.NEW_UPDATE -> {
                    needsUpdate = true;
                    updateMessage = parseVersion(section.getString("new_update", "A new update for Hubbly is available"), result);

                    logger.info(updateMessage);

                }
                case UpdateChecker.UpdateReason.UNRELEASED_VERSION -> {
                    needsUpdate = false;

                    logger.info(String.format("You're running a development build (%s)...", plugin.getDescription().getVersion()));
                    logger.info("Proceed with caution.");
                }
                default -> {
                    needsUpdate = false;
                    updateMessage = parseVersion(section.getString("error", "Could not check for a new version..."), result);

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

    public String parseVersion(String message, UpdateChecker.UpdateResult result) {
        if(message.contains("%current%")) {
            message = message.replace("%current%", Hubbly.getInstance().getDescription().getVersion());
        }
        if(message.contains("%new%")) {
            final String newest = result.getNewestVersion();
            message = message.replace("%new%", newest);

        }

        return message;
    }

    public String getMessage() {
        return this.updateMessage;
    }
}
