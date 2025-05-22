package io.hhplus.tdd.point.lock;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.entity.UserPoint;
import io.hhplus.tdd.point.service.PointService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest
public class ConcurrencyTest {

    @Autowired
    private PointService pointService;

    @Autowired
    private UserPointTable userPointTable;

    @Autowired
    private PointHistoryTable pointHistoryTable;

    @DisplayName("특정 사용자가 동시에 1000포인트를 10회 충전한다.")
    @Test
    void concurrentChargeTest() throws InterruptedException {
        long userId = 1L;
        long initialPoint = 0L;
        long chargeAmount = 1000L;
        int numberOfThreads = 10;
        long expectedFinalPoint = initialPoint + (chargeAmount * numberOfThreads);

        // 초기 포인트 설정
        userPointTable.insertOrUpdate(userId, initialPoint);

        // 스레드 풀과 CountDownLatch 설정
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        // 동시성 테스트 실행
        for (int i = 0; i < numberOfThreads; i++) {
            executorService.submit(() -> {
                try {
                    pointService.chargeUserPoint(userId, chargeAmount);
                } finally {
                    latch.countDown();
                }
            });
        }

        // 모든 스레드가 작업을 완료할 때까지 대기
        latch.await();
        executorService.shutdown();

        // 최종 포인트 검증
        UserPoint userPoint = pointService.getUserPointById(userId);
        assertEquals(expectedFinalPoint, userPoint.point());
    }

    @DisplayName("특정 사용자가 동시에 500포인트를 100회 사용한다.")
    @Test
    void concurrentUseTest() throws InterruptedException {
        long userId = 1L;
        long initialPoint = 50000L;
        long useAmount = 500L;
        int numberOfThreads = 100;
        long expectedFinalPoint = initialPoint - (useAmount * numberOfThreads);

        // 초기 포인트 설정
        pointService.chargeUserPoint(userId, initialPoint);

        // 스레드 풀과 CountDownLatch 설정
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        // 동시성 테스트 실행
        for (int i = 0; i < numberOfThreads; i++) {
            executorService.submit(() -> {
                try {
                    pointService.useUserPoint(userId, useAmount);
                } finally {
                    latch.countDown();
                }
            });
        }

        // 모든 스레드가 작업을 완료할 때까지 대기
        latch.await();
        executorService.shutdown();

        // 최종 포인트 검증
        UserPoint userPoint = pointService.getUserPointById(userId);
        assertEquals(expectedFinalPoint, userPoint.point());
    }
}
