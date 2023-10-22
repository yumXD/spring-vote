package com.vote.exception;

public class ElectionInProgressException extends RuntimeException {
    private Long id;

    public ElectionInProgressException(String message, Long id) {
        super(message);
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}