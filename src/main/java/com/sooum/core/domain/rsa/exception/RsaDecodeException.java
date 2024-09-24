package com.sooum.core.domain.rsa.exception;

public class RsaDecodeException extends RuntimeException {
    public RsaDecodeException() {
        super("RSA 복호화 중 오류가 발생했습니다.");
    }
}
