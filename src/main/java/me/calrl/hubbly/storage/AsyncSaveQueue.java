package me.calrl.hubbly.storage;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.managers.PlayerDataManager;

import java.sql.SQLException;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.*;

public class AsyncSaveQueue {

    private final BlockingQueue<UUID> queue = new LinkedBlockingQueue<>(Integer.MAX_VALUE);
    private final Set<UUID> pending = ConcurrentHashMap.newKeySet();

    private final ExecutorService worker;
    private final PlayerDataManager repository;
    private final Hubbly plugin;

    private volatile boolean running = true;

    public AsyncSaveQueue(Hubbly plugin, PlayerDataManager repository) {
        this.plugin = plugin;
        this.repository = repository;

        this.worker = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r, "hubbly-player-save-worker");
            t.setDaemon(true);
            return t;
        });

        startWorker();
    }

    /**
     * Background worker loop
     */
    private void startWorker() {
        worker.submit(() -> {
            while (running) {
                try {
                    UUID uuid = queue.take(); // blocks
                    pending.remove(uuid);

                    PlayerData data = repository.get(uuid);
                    if (data == null) {
                        continue; // player already unloaded
                    }

                    repository.saveToDB(data);

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (SQLException e) {
                    plugin.getLogger().warning("Failed async save:");
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Queue a save (fire-and-forget)
     * Deduplicates by UUID
     */
    public void queueSave(PlayerData data) {
        if (!plugin.getDatabaseManager().isActive()) return;

        UUID uuid = data.getUuid();

        if (pending.add(uuid)) {
            boolean queued = queue.offer(uuid);
            if (!queued) {
                pending.remove(uuid);
                plugin.getLogger().warning("Failed to enqueue save for " + uuid);
            }
        }
    }

    /**
     * Force immediate save (used on quit / shutdown)
     * Runs synchronously
     */
    public void saveNow(PlayerData data) {
        if (!plugin.getDatabaseManager().isActive()) return;

        try {
            repository.saveToDB(data);
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to save data for " + data.getName());
            e.printStackTrace();
        }
    }

    /**
     * Stop worker and flush remaining saves
     */
    public void shutdown() {
        running = false;

        // Flush remaining queued saves synchronously
        for (UUID uuid : queue) {
            PlayerData data = repository.get(uuid);
            if (data != null) {
                saveNow(data);
            }
        }

        worker.shutdownNow();
    }
}
