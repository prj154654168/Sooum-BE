package com.sooum.global.auth.interceptor.exception;

public class JwtBlacklistException extends RuntimeException {
    public JwtBlacklistException() {
        super("사용할 수 없는 토큰입니다.");
    }
}
