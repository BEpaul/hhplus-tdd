package io.hhplus.tdd.point.entity;

import io.hhplus.tdd.exception.ExceedsMaximumPointException;
import io.hhplus.tdd.exception.NotEnoughCurrentPointException;
import io.hhplus.tdd.point.constants.PointConstants;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public record UserPoint(
        long id,
        long point,
        long updateMillis
) {

    public static UserPoint empty(long id) {
        return new UserPoint(id, 0, System.currentTimeMillis());
    }

    public UserPoint charge(long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("충전하려는 포인트 값은 음수이거나 0일 수 없습니다.");
        }

        if (this.point + amount > PointConstants.MAXIMUM_POINT.getValue()) {
            log.warn("충전 후 포인트가 최대 포인트를 초과합니다. 현재 포인트: {}, 충전하려는 포인트: {}", this.point, amount);
            throw new ExceedsMaximumPointException("충전 후 포인트가 최대 포인트를 초과합니다.");
        }

        return new UserPoint(this.id, this.point + amount, System.currentTimeMillis());
    }

    public UserPoint use(long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("사용하려는 포인트 값은 음수이거나 0일 수 없습니다.");
        }

        if (this.point < amount) {
            log.warn("사용하려는 포인트가 현재 보유한 포인트보다 많습니다. 현재 포인트: {}, 사용하려는 포인트: {}", this.point, amount);
            throw new NotEnoughCurrentPointException("사용하려는 포인트가 현재 보유한 포인트보다 많습니다.");
        }

        return new UserPoint(this.id, this.point - amount, System.currentTimeMillis());
    }
}
