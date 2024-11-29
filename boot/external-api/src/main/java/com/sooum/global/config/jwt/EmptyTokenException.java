package com.sooum.global.config.jwt;

public class EmptyTokenException extends RuntimeException {
    public EmptyTokenException() {
        super("토큰이 존재하지 않습니다.");
    }
}
