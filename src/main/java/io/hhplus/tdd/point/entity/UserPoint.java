package io.hhplus.tdd.point.entity;

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

        return new UserPoint(this.id, this.point + amount, System.currentTimeMillis());
    }
}
