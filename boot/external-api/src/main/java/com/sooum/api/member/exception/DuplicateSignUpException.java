package com.sooum.api.member.exception;

public class DuplicateSignUpException extends RuntimeException {
    public DuplicateSignUpException() {
        super("이미 가입된 유저입니다.");
    }
}
