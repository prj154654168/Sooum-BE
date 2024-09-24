package com.sooum.core.global.exceptionmessage;

import lombok.Getter;

@Getter
public enum ExceptionMessage {
    MEMBER_NOT_FOUND("사용자를 찾을 수 없습니다."),
    CARD_NOT_FOUND("카드를 찾을 수 없습니다."),
    ALREADY_CARD_LIKED("이미 좋아요한 카드입니다"),
    ALREADY_BLOCKED("이미 차단한 사용자입니다."),
    LIKE_NOT_FOUND("좋아요를 찾을 수 업습니다.");

    private final String message;

    ExceptionMessage(String message) {
        this.message = message;
    }
}
