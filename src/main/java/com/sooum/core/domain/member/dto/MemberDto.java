package com.sooum.core.domain.member.dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;

public class MemberDto {

    @Getter
    public static class DefaultMemberResponse extends RepresentationModel<DefaultMemberResponse> {
        private long pk;
        private String nickname;
        private Link profileImgUrl;

        @Builder
        public DefaultMemberResponse (long pk, String nickname, Link profileImgUrl) {
            this.pk = pk;
            this.nickname = nickname;
            this.profileImgUrl = profileImgUrl;
        }
    }
}
