package io.hhplus.tdd.point.constants;

public enum PointConstants {
    MAXIMUM_POINT(100000L);

    private final long value;

    PointConstants(long value) {
        this.value = value;
    }

    public long getValue() {
        return value;
    }
}