package io.hhplus.tdd.lock;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class LockManager {
    private final ConcurrentHashMap<Long, Lock> userLocks = new ConcurrentHashMap<>();

    public Lock getLockForUser(long userId) {
        return userLocks.computeIfAbsent(userId, id -> new ReentrantLock());
    }

    public void cleanupLock(long userId, Lock lock) {
        if (lock.tryLock()) {
            try {
                userLocks.remove(userId, lock);
            } finally {
                lock.unlock();
            }
        }
    }
}
