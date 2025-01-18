package com.sooum.global.config.jwt.exception;

public class ExpiredRefreshTokenException extends RuntimeException {
    public ExpiredRefreshTokenException() {
        super("만료된 리프레쉬 토큰입니다");
    }
}
