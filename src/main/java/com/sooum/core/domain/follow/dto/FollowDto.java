package com.sooum.core.domain.follow.dto;

import lombok.Getter;

public class FollowDto {
    @Getter
    public static class RequestFollowDto{
        private Long userId;
    }
}
