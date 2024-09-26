package com.sooum.core.domain.card.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sooum.core.domain.card.entity.font.Font;
import com.sooum.core.domain.member.dto.MemberDto;
import com.sooum.core.domain.tag.dto.TagDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;
import java.util.List;


@Getter
public class FeedDetailCardDto extends RepresentationModel<FeedDetailCardDto> {
    private DetailFeedCard detailFeedCard;
    private MemberDto.DefaultMemberResponse member;
    private List<TagDto.ReadTagResponse> tags;

    @Builder
    public FeedDetailCardDto(DetailFeedCard detailFeedCard, MemberDto.DefaultMemberResponse member, List<TagDto.ReadTagResponse> tags) {
        this.detailFeedCard = detailFeedCard;
        this.member = member;
        this.tags = tags;
    }

    @Getter
    @Setter
    public static class DetailFeedCard extends CardDetailDto {
        @JsonProperty(value = "isStory")
        private boolean isStory;
        private LocalDateTime storyExpiredTime;

        @Builder
        public DetailFeedCard(long id, String content, Link backgroundImgUrl, Font font, Double distance, LocalDateTime createdAt, boolean isLiked, int likeCnt, boolean isOwnCard, LocalDateTime storyExpiredTime, boolean isStory) {
            super(id, content, backgroundImgUrl, font, distance, createdAt, isLiked, likeCnt, isOwnCard);
            this.storyExpiredTime = isStory ? createdAt.plusDays(1L) : null;
            this.isStory = isStory;
        }
    }

}