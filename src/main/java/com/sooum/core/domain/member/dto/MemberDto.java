package com.sooum.core.domain.member.dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;

public class MemberDto {

    @Getter
    public static class DefaultMemberResponse extends RepresentationModel<DefaultMemberResponse> {
        private long id;
        private String nickname;
        private Link profileImgUrl;

        @Builder
        public DefaultMemberResponse (long id, String nickname, Link profileImgUrl) {
            this.id = id;
            this.nickname = nickname;
            this.profileImgUrl = profileImgUrl;
        }
    }
}
