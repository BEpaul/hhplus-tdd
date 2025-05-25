package io.hhplus.tdd.point.service;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.exception.NotEnoughCurrentPointException;
import io.hhplus.tdd.point.entity.PointHistory;
import io.hhplus.tdd.point.entity.UserPoint;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static io.hhplus.tdd.point.entity.TransactionType.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest
public class PointServiceIntegrationTest {

    @Autowired
    private IPointService pointService;

    @Autowired
    private PointHistoryTable historyTable;

    @Autowired
    private UserPointTable userPointTable;

    @DisplayName("특정 ID를 가진 사용자의 포인트를 조회한다.")
    @Test
    void getPointByUserId() {
        // given
        long userId = 1L;
        long point = 1000L;
        userPointTable.insertOrUpdate(userId, point);

        // when
        UserPoint userPoint = pointService.getUserPointById(userId);

        // then
        assertEquals(userId, userPoint.id());
        assertEquals(point, userPoint.point());
    }

    @DisplayName("특정 사용자의 포인트 충전/이용 내역을 조회한다.")
    @Test
    void getPointHistoryByUserId() {
        // given
        long userId = 1L;
        long chargeAmount = 1000L;
        long useAmount = 500L;

        historyTable.insert(userId, chargeAmount, CHARGE, System.currentTimeMillis());
        historyTable.insert(userId, useAmount, USE, System.currentTimeMillis());

        // when
        List<PointHistory> histories = pointService.getPointHistoryByUserId(userId);

        // then
        assertEquals(2, histories.size());
        assertEquals(chargeAmount, histories.get(0).amount());
        assertEquals(CHARGE, histories.get(0).type());
        assertEquals(useAmount, histories.get(1).amount());
        assertEquals(USE, histories.get(1).type());
    }

    @DisplayName("양수가 아닌 포인트를 충전하려고 하면 예외가 발생한다.")
    @Test
    void chargeNegativePoint_shouldThrowsException() {
        // given
        long userId = 1L;
        long currentPoint = 5000L;
        long negativeAmount = -1000L;
        long zeroAmount = 0L;
        userPointTable.insertOrUpdate(userId, currentPoint);

        // when & then
        assertThatThrownBy(() -> pointService.chargeUserPoint(userId, negativeAmount))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> pointService.chargeUserPoint(userId, zeroAmount))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("특정 사용자의 포인트를 충전한다.")
    @Test
    void chargePoint() {
        // given
        long userId = 1L;
        long initialPoint = 5000L;
        long chargeAmount = 1000L;
        long expectedPoint = initialPoint + chargeAmount;

        userPointTable.insertOrUpdate(userId, initialPoint);

        // when
        UserPoint userPoint = pointService.chargeUserPoint(userId, chargeAmount);

        // then
        assertEquals(expectedPoint, userPoint.point());
    }

    @DisplayName("양수가 아닌 포인트를 사용하려고 하면 예외가 발생한다.")
    @Test
    void useNegativePoint_shouldThrowsException() {
        // given
        long userId = 1L;
        long currentPoint = 5000L;
        long negativeAmount = -1000L;
        long zeroAmount = 0L;
        userPointTable.insertOrUpdate(userId, currentPoint);

        // when & then
        assertThatThrownBy(() -> pointService.useUserPoint(userId, negativeAmount))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> pointService.useUserPoint(userId, zeroAmount))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("사용하려는 포인트가 보유 포인트보다 큰 경우 예외가 발생한다.")
    @Test
    void useMoreThanAvailablePoint_shouldThrowsException() {
        // given
        long userId = 1L;
        long currentPoint = 5000L;
        long useAmount = 6000L;
        userPointTable.insertOrUpdate(userId, currentPoint);

        // when & then
        assertThatThrownBy(() -> pointService.useUserPoint(userId, useAmount))
                .isInstanceOf(NotEnoughCurrentPointException.class);
    }

    @DisplayName("특정 사용자의 포인트를 사용한다.")
    @Test
    void usePoint() {
        // given
        long userId = 1L;
        long initialPoint = 5000L;
        long useAmount = 1000L;
        long expectedPoint = initialPoint - useAmount;

        userPointTable.insertOrUpdate(userId, initialPoint);

        // when
        UserPoint userPoint = pointService.useUserPoint(userId, useAmount);

        // then
        assertEquals(expectedPoint, userPoint.point());
    }
}
