package com.sooum.api.member.exception;

import java.util.NoSuchElementException;

import static com.sooum.global.exceptionmessage.ExceptionMessage.MEMBER_NOT_FOUND;

public class MemberNotFoundException extends NoSuchElementException {
    public MemberNotFoundException() {
        super(MEMBER_NOT_FOUND.getMessage());
    }
}
