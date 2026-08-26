package com.example.voting.exception;

public class InvalidElectionStateException extends RuntimeException {
    public InvalidElectionStateException(String msg) { super(msg); }
}
