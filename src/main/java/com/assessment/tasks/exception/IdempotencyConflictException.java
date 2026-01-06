package com.assessment.tasks.exception;

public class IdempotencyConflictException extends RuntimeException {

    public IdempotencyConflictException(String key) {
        super("Idempotency key already used with " + key);
    }
}