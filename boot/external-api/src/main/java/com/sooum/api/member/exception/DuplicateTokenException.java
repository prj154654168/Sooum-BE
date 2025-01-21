package com.sooum.api.member.exception;

public class DuplicateTokenException extends RuntimeException {
    public DuplicateTokenException(String message) {
        super(message);
    }
}
