package me.calrl.hubbly.storage;

import org.jetbrains.annotations.Async;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.*;

public class AsyncPlayerSaveQueue {
    private final BlockingQueue<UUID> queue = new LinkedBlockingQueue<>();
    private final Set<UUID> pending = ConcurrentHashMap.newKeySet();
    private final ExecutorService worker;

    public AsyncPlayerSaveQueue() {
        this.worker = Executors.newSingleThreadExecutor(r ->
                new Thread(r, "playerSaveWorker")
        );

    }

    private void startWorker() {}
    public void enqueue(PlayerData saveData) {}
    public void shutdownAndFlush() {
        this.worker.shutdownNow();
    }
}
