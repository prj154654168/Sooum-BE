package com.sooum.api.member.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
        private LocalDateTime banEndAt;

        @Builder
        public MemberStatus(LocalDateTime banEndAt) {
            this.banEndAt = banEndAt;
        }
    }
}
