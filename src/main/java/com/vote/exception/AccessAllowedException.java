package com.vote.exception;

public class AccessAllowedException extends RuntimeException {
    public AccessAllowedException(String message) {
        super(message);
    }
}
