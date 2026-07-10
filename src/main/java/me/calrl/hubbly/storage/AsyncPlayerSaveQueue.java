package me.calrl.hubbly.storage;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.function.Consumer;

public class AsyncPlayerSaveQueue {
    private final BlockingQueue<UUID> queue = new LinkedBlockingQueue<>();
    private final ConcurrentMap<UUID, PlayerData> latestSnapshots = new ConcurrentHashMap<>();
    private final Set<UUID> pending = ConcurrentHashMap.newKeySet();
    private final ExecutorService worker;
    private final Consumer<PlayerData> saveFunction;
    private volatile boolean running = true;

    public AsyncPlayerSaveQueue(Consumer<PlayerData> saveFunction) {
        this.saveFunction = saveFunction;
        this.worker = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r, "Hubbly-PlayerSaveWorker");
            t.setDaemon(true);
            return t;
        });

        startWorker();
    }

    private void startWorker() {
        worker.submit(() -> {
            while (running || !queue.isEmpty() || !latestSnapshots.isEmpty()) {
                UUID uuid = null;
                try {
                    uuid = queue.poll(1, TimeUnit.SECONDS);
                    if (uuid == null) continue;

                    PlayerData data = latestSnapshots.remove(uuid);
                    if (data == null) {
                        pending.remove(uuid);
                        continue;
                    }

                    saveFunction.accept(data);

                } catch (InterruptedException ignored) {
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (uuid != null) {
                        pending.remove(uuid);
                        queueLatestSnapshot(uuid);
                    }
                }
            }
        });
    }

    /**
     * Fire-and-forget save. Duplicate saves per player are coalesced.
     */
    public void enqueue(PlayerData snapshot) {
        UUID uuid = snapshot.getUuid();
        latestSnapshots.put(uuid, snapshot);
        queueLatestSnapshot(uuid);
    }

    private void queueLatestSnapshot(UUID uuid) {
        if (!latestSnapshots.containsKey(uuid) || !pending.add(uuid)) {
            return;
        }

        boolean success = queue.offer(uuid);

        if (!success) {
            pending.remove(uuid);
            throw new IllegalStateException("Failed to enqueue player save task");
        }
    }

    /**
     * Flush remaining saves and stop worker.
     */
    public void shutdownAndFlush() {
        running = false;
        worker.shutdown();
        try {
            worker.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException ignored) {}
    }
}
