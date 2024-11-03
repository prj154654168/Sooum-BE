package com.sooum.api.rsa.exception;

public class RsaGenerateException extends RuntimeException {
    public RsaGenerateException() {
        super("RSA 키 생성 중 오류가 발생했습니다.");
    }
}
