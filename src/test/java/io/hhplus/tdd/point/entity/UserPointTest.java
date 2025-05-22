package io.hhplus.tdd.point.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserPointTest {

    @DisplayName("충전하려는 포인트 값이 음수이거나 0인 경우 예외가 발생한다.")
    @Test
    void chargeNegativePoint_shouldThrowsException() {
        // given
        long userId = 1L;
        long negativeAmount = -1000L;
        long zeroAmount = 0L;
        UserPoint currentUserPoint = new UserPoint(userId, 5000L, System.currentTimeMillis());

        // when & then
        assertThrows(IllegalArgumentException.class, () -> {
            currentUserPoint.charge(negativeAmount);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            currentUserPoint.charge(zeroAmount);
        });
    }

    @DisplayName("충전하려는 포인트 값이 양수이면 사용자의 포인트를 정상적으로 충전한다.")
    @Test
    void chargePoint() {
        // given
        long userId = 1L;
        long chargeAmount = 1000L;
        UserPoint currentUserPoint = new UserPoint(userId, 5000L, System.currentTimeMillis());
        UserPoint expectedUserPoint = new UserPoint(userId, 6000L, System.currentTimeMillis());

        // when
        UserPoint userPoint = currentUserPoint.charge(chargeAmount);

        // then
        assertEquals(expectedUserPoint.point(), userPoint.point());
    }
}
