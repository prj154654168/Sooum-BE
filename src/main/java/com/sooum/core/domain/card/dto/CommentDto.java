package com.sooum.core.domain.card.dto;

import com.sooum.core.domain.card.entity.font.Font;
import com.sooum.core.domain.card.entity.fontsize.FontSize;
import com.sooum.core.domain.card.entity.parenttype.CardType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;

public class CommentDto {
    @Getter
    @NoArgsConstructor
    public static class CommentCntRetrieve extends RepresentationModel<CommentCntRetrieve> {
        private int commentCnt;

        @Builder
        public CommentCntRetrieve(int commentCnt) {
            this.commentCnt = commentCnt;
        }
    }

    @Getter
    @Setter
    public static class CommentCardsInfo extends CardDto {
        private Double distance;
        private CardType cardType;

        @Builder
        public CommentCardsInfo(long id, boolean isStory, String content, LocalDateTime createdAt, int likeCnt, boolean isLiked, int commentCnt, boolean isCommentWritten, Link backgroundImgUrl, Font font, FontSize fontSize, Double distance, CardType cardType) {
            super(id, isStory, content, createdAt, likeCnt, isLiked, commentCnt, isCommentWritten, backgroundImgUrl, font, fontSize);
            this.distance = distance;
            this.cardType = cardType;
        }
    }
}
