package me.calrl.hubbly.utils.update;

import me.calrl.hubbly.Hubbly;
import org.bukkit.configuration.ConfigurationSection;
import org.checkerframework.checker.units.qual.C;

import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

public class UpdateUtil {

    private String updateMessage = null;
    private boolean needsUpdate;
    private String key;
    private String currentVersion;
    private String newVersion;
    public boolean checkForUpdate(Hubbly plugin) {
        Logger logger = plugin.getLogger();

        CompletableFuture<Boolean> updateFuture = new CompletableFuture<>();

        UpdateChecker.init(plugin, 117243).requestUpdateCheck().whenComplete((result, exception) -> {
            UpdateChecker.UpdateReason reason = result.getReason();
            this.currentVersion = plugin.getDescription().getVersion();

            //todo: remove the updateMessage down the line as it uses old logic (removed in 3.0.0)
            switch(reason) {
                case UpdateChecker.UpdateReason.UP_TO_DATE -> {
                    needsUpdate = false;
                    key = "update.no_update";
                    updateMessage = "No update";
                    logger.info(updateMessage);
                    }
                case UpdateChecker.UpdateReason.NEW_UPDATE -> {
                    needsUpdate = true;
                    key = "update.new_update";
                    updateMessage = parsePlaceholders("A new update for Hubbly is available: %new%", result);
                    this.setNew(result.getNewestVersion());

                    logger.info(updateMessage);

                }
                case UpdateChecker.UpdateReason.UNRELEASED_VERSION -> {
                    needsUpdate = false;

                    key = "update.no_update";

                    updateMessage = String.format("You're running a development build (%s)...", plugin.getDescription().getVersion());
                    logger.info(updateMessage);
                    logger.info("Proceed with caution.");
                }
                default -> {
                    needsUpdate = false;
                    key = "update.error";
                    updateMessage = "Could not check for a new version...";

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
    public String getKey() { return this.key;}

    public void setCurrent(String version) {
        this.currentVersion = version;
    }

    public void setNew(String version) {
        this.newVersion = version;
    }

    public String getCurrent() {
        return this.currentVersion;
    }

    public String getNew() {
        return this.newVersion;
    }
}
