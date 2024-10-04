package com.sooum.core.domain.card.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sooum.core.domain.card.entity.font.Font;
import com.sooum.core.domain.member.dto.MemberDto;
import com.sooum.core.domain.tag.dto.TagDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.Link;

import java.time.LocalDateTime;
import java.util.List;


@Getter
@Setter
public class FeedDetailCardDto extends CardDetailDto {
    @JsonProperty(value = "isStory")
    private boolean isStory;
    private LocalDateTime storyExpirationTime;

    @Builder
    public FeedDetailCardDto(Font font, String id, String content, Link backgroundImgUrl, Double distance, LocalDateTime createdAt, boolean isOwnCard, MemberDto.DefaultMemberResponse member, List<TagDto.ReadTagResponse> tags, boolean isStory) {
        super(font, id, content, backgroundImgUrl, distance, createdAt, isOwnCard, member, tags);
        this.isStory = isStory;
        this.storyExpirationTime = isStory ? createdAt.plusDays(1L) : null;
    }
}