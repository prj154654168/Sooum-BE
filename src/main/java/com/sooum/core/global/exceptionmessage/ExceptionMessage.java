package com.sooum.core.global.exceptionmessage;

import lombok.Getter;

@Getter
public enum ExceptionMessage {
    MEMBER_NOT_FOUND("사용자를 찾을 수 없습니다."),
    CARD_NOT_FOUND("카드를 찾을 수 없습니다."),
    ALREADY_CARD_LIKED("이미 좋아요한 카드입니다");

    private final String message;

    ExceptionMessage(String message) {
        this.message = message;
    }
}
