package com.example.voting.exception;

public class InvalidVotingAuthorizationException extends RuntimeException {
    public InvalidVotingAuthorizationException(String msg) { super(msg); }
}
