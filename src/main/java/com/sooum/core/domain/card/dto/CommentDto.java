package com.sooum.core.domain.card.dto;

import com.sooum.core.domain.card.entity.font.Font;
import com.sooum.core.domain.card.entity.fontsize.FontSize;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.Link;

import java.time.LocalDateTime;

public class CommentDto {
    @Getter
    public static class CommentCardsInfo extends CardDto {
        private Double distance;

        @Builder
        public CommentCardsInfo(String id, String content, LocalDateTime createdAt, int likeCnt, boolean isLiked, int commentCnt, boolean isCommentWritten, Link backgroundImgUrl, Font font, FontSize fontSize, Double distance) {
            super(id, content, createdAt, likeCnt, isLiked, commentCnt, isCommentWritten, backgroundImgUrl, font, fontSize);
            this.distance = distance;
        }
    }
}
