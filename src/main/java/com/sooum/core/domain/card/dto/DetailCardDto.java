package com.sooum.core.domain.card.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sooum.core.domain.card.entity.font.Font;
import com.sooum.core.domain.card.entity.fontsize.FontSize;
import com.sooum.core.domain.member.dto.MemberDto;
import com.sooum.core.domain.tag.dto.TagDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;
import java.util.List;


public class DetailCardDto {

    @Getter
    public static class DetailFeedCardRetrieve extends RepresentationModel<DetailFeedCardRetrieve> {
        private DetailFeedCard detailFeedCard;
        private MemberDto.DefaultMemberResponse member;
        private List<TagDto.ReadTagResponse> tags;

        @Builder
        public DetailFeedCardRetrieve(DetailFeedCard detailFeedCard, MemberDto.DefaultMemberResponse member, List<TagDto.ReadTagResponse> tags) {
            this.detailFeedCard = detailFeedCard;
            this.member = member;
            this.tags = tags;
        }
    }

    @Getter
    @Setter
    public static class DetailFeedCard extends RepresentationModel<DetailFeedCard> {
        private long id;
        private String content;
        @JsonProperty(value = "isStory")
        private boolean isStory;
        private LocalDateTime storyExpiredTime;
        private Link backgroundImgUrl;
        private Font font;
        private Double distance;
        private LocalDateTime createdAt;
        @JsonProperty(value = "isLiked")
        private boolean isLiked;
        private int likeCnt;
        @JsonProperty(value = "isOwnCard")
        private boolean isOwnCard;

        @Builder
        public DetailFeedCard(long id, String content, boolean isStory, LocalDateTime storyExpiredTime, Link backgroundImgUrl, Font font, Double distance, LocalDateTime createdAt, boolean isLiked, int likeCnt, boolean isOwnCard) {
            this.id = id;
            this.content = content;
            this.isStory = isStory;
            this.storyExpiredTime = storyExpiredTime;
            this.backgroundImgUrl = backgroundImgUrl;
            this.font = font;
            this.distance = distance;
            this.createdAt = createdAt;
            this.isLiked = isLiked;
            this.likeCnt = likeCnt;
            this.isOwnCard = isOwnCard;
        }
    }
}