package com.sooum.core.domain.member.exception;

import jakarta.persistence.EntityNotFoundException;

import static com.sooum.core.global.exceptionmessage.ExceptionMessage.MEMBER_NOT_FOUND;

public class MemberNotFoundException extends EntityNotFoundException {
    public MemberNotFoundException() {
        super(MEMBER_NOT_FOUND.getMessage());
    }
}
