package com.sooum.api.member.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;

public class MemberDto {

    @Getter
    public static class DefaultMemberResponse extends RepresentationModel<DefaultMemberResponse> {
        private String id;
        private String nickname;
        private Link profileImgUrl;

        @Builder
        public DefaultMemberResponse (String id, String nickname, Link profileImgUrl) {
            this.id = id;
            this.nickname = nickname;
            this.profileImgUrl = profileImgUrl;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class MemberStatus {
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
        private LocalDateTime banEndAt;

        @Builder
        public MemberStatus(LocalDateTime banEndAt) {
            this.banEndAt = banEndAt;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class FCMTokenUpdateRequest {
        private String fcmToken;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class NotifyAllowUpdateRequest {
        @JsonProperty("isAllowNotify")
        private boolean isAllowNotify;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class NotifyAllowResponse {
        @JsonProperty("isAllowNotify")
        private boolean isAllowNotify;

        @Builder
        public NotifyAllowResponse(boolean isAllowNotify) {
            this.isAllowNotify = isAllowNotify;
        }
    }
}
