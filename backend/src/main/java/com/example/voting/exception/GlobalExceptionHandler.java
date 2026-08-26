package com.example.voting.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ElectionNotFoundException.class)
    public ResponseEntity<?> handleNotFound(ElectionNotFoundException ex) {
        return json(HttpStatus.NOT_FOUND, "ELECTION_NOT_FOUND", ex.getMessage());
    }

    @ExceptionHandler(InvalidElectionStateException.class)
    public ResponseEntity<?> handleInvalidState(InvalidElectionStateException ex) {
        return json(HttpStatus.BAD_REQUEST, "INVALID_ELECTION_STATE", ex.getMessage());
    }

    @ExceptionHandler(AlreadyVotedException.class)
    public ResponseEntity<?> handleAlreadyVoted(AlreadyVotedException ex) {
        return json(HttpStatus.CONFLICT, "ALREADY_VOTED", ex.getMessage());
    }

    @ExceptionHandler(InvalidVotingAuthorizationException.class)
    public ResponseEntity<?> handleInvalidAuth(InvalidVotingAuthorizationException ex) {
        return json(HttpStatus.FORBIDDEN, "INVALID_VOTING_AUTHORIZATION", ex.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleDataIntegrity(DataIntegrityViolationException ex) {
        return json(HttpStatus.CONFLICT, "DATA_INTEGRITY", ex.getMostSpecificCause().getMessage());
    }

    @ExceptionHandler(com.example.voting.exception.RateLimitExceededException.class)
    public ResponseEntity<?> handleRateLimit(com.example.voting.exception.RateLimitExceededException ex) {
        return json(HttpStatus.TOO_MANY_REQUESTS, "RATE_LIMIT_EXCEEDED", ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidation(MethodArgumentNotValidException ex) {
        return json(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", ex.getBindingResult().getFieldErrors().stream().map(fe -> fe.getField() + ": " + fe.getDefaultMessage()).reduce((a,b)->a+";"+b).orElse(ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneric(Exception ex) {
        return json(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", ex.getMessage());
    }

    private ResponseEntity<?> json(HttpStatus status, String code, String msg) {
        Map<String,String> m = new HashMap<>();
        m.put("error", code);
        m.put("message", msg);
        return ResponseEntity.status(status).body(m);
    }
}
