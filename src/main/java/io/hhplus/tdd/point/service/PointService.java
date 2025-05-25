package io.hhplus.tdd.point.service;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.entity.PointHistory;
import io.hhplus.tdd.point.entity.UserPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.Lock;

import io.hhplus.tdd.lock.LockManager;

import static io.hhplus.tdd.point.entity.TransactionType.*;

@Service
@RequiredArgsConstructor
public class PointService implements IPointService {

    private final UserPointTable userPointTable;
    private final PointHistoryTable pointHistoryTable;
    private final LockManager lockManager = new LockManager();

    @Override
    public UserPoint getUserPointById(long id) {
        return userPointTable.selectById(id);
    }

    @Override
    public List<PointHistory> getPointHistoryByUserId(long userId) {
        return pointHistoryTable.selectAllByUserId(userId);
    }

    @Override
    public UserPoint chargeUserPoint(long userId, long amount) {
        Lock lock = lockManager.getLockForUser(userId);
        lock.lock();
        UserPoint chargedUserPoint;
        try {
            UserPoint userPoint = userPointTable.selectById(userId);
            chargedUserPoint = userPoint.charge(amount);
            userPointTable.insertOrUpdate(userId, chargedUserPoint.point());
        } finally {
            lock.unlock();
            lockManager.cleanupLock(userId, lock);
        }

        // 비동기적으로 포인트 히스토리 기록
        CompletableFuture.runAsync(() ->
            pointHistoryTable.insert(userId, amount, CHARGE, System.currentTimeMillis())
        );

        return chargedUserPoint;
    }

    @Override
    public UserPoint useUserPoint(long userId, long amount) {
        Lock lock = lockManager.getLockForUser(userId);
        lock.lock();
        UserPoint usedUserPoint;
        try {
            UserPoint userPoint = userPointTable.selectById(userId);
            usedUserPoint = userPoint.use(amount);
            userPointTable.insertOrUpdate(userId, usedUserPoint.point());
        } finally {
            lock.unlock();
            lockManager.cleanupLock(userId, lock);
        }

        // 비동기적으로 포인트 히스토리 기록
        CompletableFuture.runAsync(() ->
            pointHistoryTable.insert(userId, amount, USE, System.currentTimeMillis())
        );

        return usedUserPoint;
    }
}
