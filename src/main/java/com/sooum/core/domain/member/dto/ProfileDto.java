package com.sooum.core.domain.member.dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.hateoas.Link;

public class ProfileDto {
    @Getter
    @Builder
    public static class ProfileInfoResponse{
        private String nickname;
        private String currentDayVisitors;
        private String totalVisitorCnt;
        private Link profileImg;
        private String cardCnt;
        private String followingCnt;
        private String followerCnt;
    }
}
