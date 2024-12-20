package com.sooum.api.follow.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;

@Getter
@Setter
public class FollowInfoDto extends RepresentationModel<FollowInfoDto> {
    private final String id;
    private final String nickname;
    private final Link backgroundImgUrl;
    @JsonProperty(value = "isFollowing")
    private boolean isFollowing;

    public FollowInfoDto(Link backgroundImgUrl, String id, String nickname, boolean isFollowing) {
        this.backgroundImgUrl = backgroundImgUrl;
        this.id = id;
        this.nickname = nickname;
        this.isFollowing = isFollowing;
    }
}
