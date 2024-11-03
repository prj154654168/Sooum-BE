package com.sooum.api.member.exception;

public class PolicyNotAllowException extends RuntimeException {
    public PolicyNotAllowException() {
        super("이용 약관을 동의하지 않았습니다.");
    }
}
