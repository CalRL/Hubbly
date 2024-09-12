package me.calrl.hubbly.utils.update;

import me.calrl.hubbly.Hubbly;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

public class UpdateUtil {

    public boolean checkForUpdate(Hubbly plugin) {
        AtomicBoolean needsUpdate = new AtomicBoolean(false);
        Logger logger = plugin.getLogger();

        UpdateChecker.init(plugin, 117243).requestUpdateCheck().whenComplete((result, exception) -> {
            UpdateChecker.UpdateReason reason = result.getReason();

            switch(reason) {
                case UpdateChecker.UpdateReason.UP_TO_DATE -> {
                    logger.info(String.format("Hubbly (%s) is up to date", result.getNewestVersion()));
                    }
                case UpdateChecker.UpdateReason.NEW_UPDATE -> {
                    needsUpdate.set(true);
                    logger.info(String.format("An update is available! Hubbly %s can be downloaded on SpigotMC", result.getNewestVersion()));

                }
                case UpdateChecker.UpdateReason.UNRELEASED_VERSION -> {
                    needsUpdate.set(false);
                    logger.info(String.format("You're running a development build (%s)...", plugin.getDescription().getVersion()));
                    logger.info("Proceed with caution.");
                }
                default -> {
                    logger.info("Could not check for a new version...");
                    logger.info("Reason: " + reason);
                }
            }
        });
        return needsUpdate.get();
    }
}
