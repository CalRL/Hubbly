package me.calrl.hubbly.storage;

import me.calrl.hubbly.enums.data.PlayerMovementMode;
import me.calrl.hubbly.enums.data.PlayerVisibilityMode;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BooleanSupplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AsyncPlayerSaveQueueTests {

    @Test
    void failedSaveDoesNotBlockFutureSavesForSamePlayer() throws Exception {
        UUID uuid = UUID.randomUUID();
        AtomicInteger attempts = new AtomicInteger();
        CountDownLatch firstAttempt = new CountDownLatch(1);

        AsyncPlayerSaveQueue queue = new AsyncPlayerSaveQueue(data -> {
            int attempt = attempts.incrementAndGet();
            if (attempt == 1) {
                firstAttempt.countDown();
                throw new ExpectedSaveFailure();
            }
        });

        try {
            queue.enqueue(playerData(uuid, "first"));
            assertTrue(firstAttempt.await(2, TimeUnit.SECONDS), "first save was not attempted");

            queue.enqueue(playerData(uuid, "second"));

            assertTrue(waitUntil(() -> attempts.get() == 2), "second save was not attempted");
        } finally {
            queue.shutdownAndFlush();
        }
    }

    @Test
    void pendingSaveUsesNewestSnapshotForPlayer() throws Exception {
        UUID uuid = UUID.randomUUID();
        CountDownLatch firstSaveStarted = new CountDownLatch(1);
        CountDownLatch releaseFirstSave = new CountDownLatch(1);
        List<String> savedNames = new CopyOnWriteArrayList<>();

        AsyncPlayerSaveQueue queue = new AsyncPlayerSaveQueue(data -> {
            savedNames.add(data.getName());
            if (savedNames.size() == 1) {
                firstSaveStarted.countDown();
                try {
                    releaseFirstSave.await(2, TimeUnit.SECONDS);
                } catch (InterruptedException ignored) {}
            }
        });

        try {
            queue.enqueue(playerData(uuid, "first"));
            assertTrue(firstSaveStarted.await(1, TimeUnit.SECONDS), "first save was not started");

            queue.enqueue(playerData(uuid, "second"));
            queue.enqueue(playerData(uuid, "third"));
            releaseFirstSave.countDown();

            assertTrue(waitUntil(() -> savedNames.size() == 2), "latest pending save was not flushed");
            assertEquals(List.of("first", "third"), savedNames);
        } finally {
            releaseFirstSave.countDown();
            queue.shutdownAndFlush();
        }
    }

    private static PlayerData playerData(UUID uuid, String name) {
        return new PlayerData(
                uuid,
                name,
                new PlayerMovementData(PlayerMovementMode.NONE),
                new PlayerVisibilityData(PlayerVisibilityMode.VISIBLE)
        );
    }

    private static boolean waitUntil(BooleanSupplier condition) throws InterruptedException {
        long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(1);
        while (System.nanoTime() < deadline) {
            if (condition.getAsBoolean()) {
                return true;
            }
            Thread.sleep(10);
        }
        return condition.getAsBoolean();
    }

    private static class ExpectedSaveFailure extends RuntimeException {
        @Override
        public synchronized Throwable fillInStackTrace() {
            return this;
        }
    }
}
