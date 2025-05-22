package io.hhplus.tdd.point.entity;

import io.hhplus.tdd.exception.NotEnoughCurrentPointException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

    @DisplayName("사용하려는 포인트 값이 음수이거나 0인 경우 예외가 발생한다.")
    @Test
    void useNegativePoint_shouldThrowsException() {
        // given
        long userId = 1L;
        long negativeAmount = -1000L;
        long zeroAmount = 0L;
        UserPoint currentUserPoint = new UserPoint(userId, 5000L, System.currentTimeMillis());

        // when & then
        assertThrows(IllegalArgumentException.class, () -> {
            currentUserPoint.use(negativeAmount);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            currentUserPoint.use(zeroAmount);
        });
    }

    @DisplayName("사용하려고 하는 포인트가 보유 포인트보다 큰 경우 예외가 발생한다.")
    @Test
    void useMoreThanAvailablePoint_shouldThrowsException() {
        // given
        long userId = 1L;
        long currentPoint = 5000L;
        long useAmount = 8000L;
        UserPoint userPoint = new UserPoint(userId, currentPoint, System.currentTimeMillis());

        // when & then
        assertThatThrownBy(() -> userPoint.use(useAmount))
                .isInstanceOf(NotEnoughCurrentPointException.class)
                .hasMessageContaining("사용하려는 포인트가 현재 보유한 포인트보다 많습니다.");
    }

    @DisplayName("사용하려는 포인트 값이 양수이고 보유 포인트보다 작은 경우 정상적으로 포인트를 사용한다.")
    @Test
    void usePoint() {
        // given
        long userId = 1L;
        long currentPoint = 5000L;
        long useAmount = 3000L;
        long expectedPoint = currentPoint - useAmount;
        UserPoint currentUserPoint = new UserPoint(userId, currentPoint, System.currentTimeMillis());

        // when
        UserPoint userPoint = currentUserPoint.use(useAmount);

        // then
        assertEquals(currentUserPoint.id(), userPoint.id());
        assertEquals(expectedPoint, userPoint.point());
    }
}
