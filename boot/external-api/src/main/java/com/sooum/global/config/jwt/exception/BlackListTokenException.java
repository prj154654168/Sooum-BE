package com.sooum.global.config.jwt.exception;

public class BlackListTokenException extends RuntimeException {
    public BlackListTokenException() {
        super("사용 정지된 토큰입니다.");
    }
}
