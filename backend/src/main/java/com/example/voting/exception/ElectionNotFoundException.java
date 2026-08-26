package com.example.voting.exception;

public class ElectionNotFoundException extends RuntimeException {
    public ElectionNotFoundException(String msg) { super(msg); }
}
