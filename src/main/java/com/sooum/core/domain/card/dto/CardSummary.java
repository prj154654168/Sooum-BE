package com.sooum.core.domain.card.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CardSummary {
    private int commentCnt;
    private int cardLikeCnt;
    @JsonProperty(value = "isLiked")
    private boolean isLiked;

    @Builder
    public CardSummary(int commentCnt, int cardLikeCnt, boolean isLiked) {
        this.commentCnt = commentCnt;
        this.cardLikeCnt = cardLikeCnt;
        this.isLiked = isLiked;
    }
}