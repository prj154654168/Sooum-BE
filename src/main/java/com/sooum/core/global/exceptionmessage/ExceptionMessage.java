package com.sooum.core.global.exceptionmessage;

import lombok.Getter;

@Getter
public enum ExceptionMessage {
    MEMBER_NOT_FOUND("사용자를 찾을 수 없습니다."),
    CARD_NOT_FOUND("카드를 찾을 수 없습니다."),
    ALREADY_CARD_LIKED("이미 좋아요한 카드입니다"),
    ALREADY_BLOCKED("이미 차단한 사용자입니다."),
    LIKE_NOT_FOUND("좋아요를 찾을 수 업습니다."),
    UNHANDLED_TYPE("처리되지 않은 타입입니다."),
    UNHANDLED_OBJECT("처리되지 않은 객체입니다."),
    IMAGE_NOT_FOUND("이미지 파일이 존재하지 않습니다."),
    TAGS_NOT_ALLOWED_FOR_STORY("스토리일 경우 태그가 존재할 수 없습니다."),
    TAG_NOT_FOUND("태그를 찾을 수 없습니다."),
    ALREADY_TAG_FAVORITE("이미 즐겨찾기한 태그입니다"),
    UNSUPPORTED_IMAGE_FORMAT("지원하지 않는 확장자입니다."),
    IMAGE_REJECTED_BY_MODERATION("부적절한 이미지 파일입니다."),
    FAVORITE_TAG_NOT_FOUND("즐겨찾기한 기록을 찾을 수 없습니다."),
    ALREADY_Following("이미 팔로우하고 있는 사용자입니다."),
    ACCOUNT_TRANSFER_NOT_FOUND("계정 이관 코드를 존재하지 않습니다."),
    ALREADY_DELETE_CARD_LIKE("이미 삭제된 좋아요입니다.");

    private final String message;

    ExceptionMessage(String message) {
        this.message = message;
    }
}
