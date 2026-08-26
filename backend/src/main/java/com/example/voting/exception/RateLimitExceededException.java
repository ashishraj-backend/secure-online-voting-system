package com.example.voting.exception;

public class RateLimitExceededException extends RuntimeException {
    public RateLimitExceededException(String msg) { super(msg); }
}
