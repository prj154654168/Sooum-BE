package com.sooum.api.member.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.hateoas.Link;

public class ProfileDto {

    @Getter
    public static class MyProfileInfoResponse {
        private final String nickname;
        private final String currentDayVisitors;
        private final String totalVisitorCnt;
        private final Link profileImg;
        private final String cardCnt;
        private final String followingCnt;
        private final String followerCnt;

        @Builder
        public MyProfileInfoResponse(String nickname, Long currentDayVisitors, Long totalVisitorCnt, Link profileImg, Long cardCnt, Long followingCnt, Long followerCnt) {
            this.nickname = nickname;
            this.currentDayVisitors = String.valueOf(currentDayVisitors);
            this.totalVisitorCnt = String.valueOf(totalVisitorCnt);
            this.profileImg = profileImg;
            this.cardCnt = String.valueOf(cardCnt);
            this.followingCnt = String.valueOf(followingCnt);
            this.followerCnt = String.valueOf(followerCnt);
        }
    }

    @Getter
    @Setter
    public static class ProfileInfoResponse{
        private String nickname;
        private String currentDayVisitors;
        private String totalVisitorCnt;
        private Link profileImg;
        private String cardCnt;
        private String followingCnt;
        private String followerCnt;
        @JsonProperty(value = "isFollowing")
        private boolean isFollowing;

        @Builder
        public ProfileInfoResponse(String nickname, Long currentDayVisitors, Long totalVisitorCnt, Link profileImg, Long cardCnt, Long followingCnt, Long followerCnt, boolean isFollowing) {
            this.nickname = nickname;
            this.currentDayVisitors = String.valueOf(currentDayVisitors);
            this.totalVisitorCnt = String.valueOf(totalVisitorCnt);
            this.profileImg = profileImg;
            this.cardCnt = String.valueOf(cardCnt);
            this.followingCnt = String.valueOf(followingCnt);
            this.followerCnt = String.valueOf(followerCnt);
            this.isFollowing = isFollowing;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class NicknameAvailableRequest {
        private String nickname;
    }

    @Getter
    @Setter
    public static class NicknameAvailableResponse {
        @JsonProperty(value = "isAvailable")
        private boolean isAvailable;

        @Builder
        public NicknameAvailableResponse(boolean isAvailable) {
            this.isAvailable = isAvailable;
        }
    }

    @Getter
    public static class ProfileUpdate {
        @Size(min = 1, max = 8)
        private final String nickname;
        private final String profileImg;

        @Builder
        public ProfileUpdate(String nickname, String profileImg) {
            this.nickname = nickname;
            this.profileImg = profileImg;
        }
    }

    @Getter
    public static class AccountTransferCodeResponse {
        private final String transferCode;

        @Builder
        public AccountTransferCodeResponse(String transferCode) {
            this.transferCode = transferCode;
        }
    }
}
