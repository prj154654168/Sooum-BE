package com.sooum.api.member.exception;

public class BannedUserException extends RuntimeException {
    public BannedUserException(String message) {
        super(message);
    }
}
