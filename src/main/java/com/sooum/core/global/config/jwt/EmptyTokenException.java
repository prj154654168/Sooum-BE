package com.sooum.core.global.config.jwt;

public class EmptyTokenException extends RuntimeException {
    public EmptyTokenException() {
        super("토큰이 비어있습니다.");
    }
}
