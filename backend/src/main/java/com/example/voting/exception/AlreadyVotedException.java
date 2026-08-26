package com.example.voting.exception;

public class AlreadyVotedException extends RuntimeException {
    public AlreadyVotedException(String msg) { super(msg); }
}
