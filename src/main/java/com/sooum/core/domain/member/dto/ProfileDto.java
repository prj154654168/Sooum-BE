package com.sooum.core.domain.member.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
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

    @Getter
    @Setter
    public static class NicknameAvailable {
        @JsonProperty(value = "isAvailable")
        private boolean isAvailable;

        @Builder
        public NicknameAvailable(boolean isAvailable) {
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
