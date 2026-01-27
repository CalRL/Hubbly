package me.calrl.hubbly.storage;

import org.jetbrains.annotations.Async;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.function.Consumer;

public class AsyncPlayerSaveQueue {
    private final BlockingQueue<PlayerData> queue = new LinkedBlockingQueue<>();
    private final Set<UUID> pending = ConcurrentHashMap.newKeySet();
    private final ExecutorService worker;
    private final Consumer<PlayerData> saveFunction;
    private volatile boolean running = true;

    public AsyncPlayerSaveQueue(Consumer<PlayerData> saveFunction) {
        this. saveFunction = saveFunction;
        this.worker = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r, "Hubbly-PlayerSaveWorker");
            t.setDaemon(true);
            return t;
        });

        startWorker();
    }

    private void startWorker() {
        worker.submit(() -> {
            while (running || !queue.isEmpty()) {
                try {
                    PlayerData data = queue.poll(1, TimeUnit.SECONDS);
                    if (data == null) continue;

                    saveFunction.accept(data);
                    pending.remove(data.getUuid());

                } catch (InterruptedException ignored) {
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Fire-and-forget save. Duplicate saves per player are coalesced.
     */
    public void enqueue(PlayerData snapshot) {
        UUID uuid = snapshot.getUuid();
        if (!pending.add(uuid)) {
            return;
        }

        boolean success = queue.offer(snapshot);

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
