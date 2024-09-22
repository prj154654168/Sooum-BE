package com.sooum.core.domain.card.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sooum.core.domain.card.dto.popularitytype.PopularityType;
import com.sooum.core.domain.card.entity.font.Font;
import com.sooum.core.domain.card.entity.fontsize.FontSize;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;

public class PopularCardDto {
    @Getter
    @Setter
    @NoArgsConstructor
    public static class PopularCardRetrieve extends RepresentationModel<PopularCardRetrieve> {
        private long id;
        private String contents;
        @JsonProperty(value = "isStory")
        private boolean isStory;
        private LocalDateTime storyExpiredTime;
        private Link backgroundImgUrl;
        private Font font;
        private FontSize fontSize;
        private Double distance;
        private LocalDateTime createdAt;
        @JsonProperty(value = "isLiked")
        private boolean isLiked;
        private int likeCnt;
        @JsonProperty(value = "isCommentWritten")
        private boolean isCommentWritten;
        private int commentCnt;

        @Builder
        public PopularCardRetrieve(long id, String contents, boolean isStory, Link backgroundImgUrl, Font font, FontSize fontSize, Double distance, LocalDateTime createdAt, boolean isLiked, int likeCnt, boolean isCommentWritten, int commentCnt) {
            this.id = id;
            this.contents = contents;
            this.isStory = isStory;
            this.storyExpiredTime = isStory ? createdAt.plusDays(1L) : null;
            this.backgroundImgUrl = backgroundImgUrl;
            this.font = font;
            this.fontSize = fontSize;
            this.distance = distance;
            this.createdAt = createdAt;
            this.isLiked = isLiked;
            this.likeCnt = likeCnt;
            this.isCommentWritten = isCommentWritten;
            this.commentCnt = commentCnt;
        }
    }
}
