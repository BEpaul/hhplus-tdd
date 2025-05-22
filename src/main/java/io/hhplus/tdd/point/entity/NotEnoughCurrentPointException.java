package io.hhplus.tdd.point.entity;

public class NotEnoughCurrentPointException extends RuntimeException {
    public NotEnoughCurrentPointException(String message) {
        super(message);
    }
} 