package com.sooum.core.domain.member.exception;

import jakarta.persistence.EntityNotFoundException;

public class MemberNotFoundException extends EntityNotFoundException {
    public MemberNotFoundException() {
        super("존재하지 않는 유저입니다.");
    }
}
