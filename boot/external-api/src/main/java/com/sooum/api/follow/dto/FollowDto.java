package com.sooum.api.follow.dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.hateoas.Link;

public class FollowDto {
    @Getter
    public static class RequestFollowDto{
        private Long userId;
    }

    @Getter
    public static class FollowerInfo extends FollowInfoDto {

        @Builder
        public FollowerInfo(boolean isFollowing, Link backgroundImgUrl, String id, String nickname) {
            super(backgroundImgUrl, id, nickname, isFollowing);
        }
    }

    @Getter
    public static class FollowingInfo extends FollowInfoDto {

        @Builder
        public FollowingInfo(boolean isFollowing, Link backgroundImgUrl, String id, String nickname) {
            super(backgroundImgUrl, id, nickname, isFollowing);
        }
    }
}
