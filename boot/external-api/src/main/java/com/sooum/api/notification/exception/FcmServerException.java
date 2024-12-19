package com.sooum.api.notification.exception;

public class FcmServerException extends RuntimeException {
    public FcmServerException() {
        super("Firebase Server Error");
    }
}
