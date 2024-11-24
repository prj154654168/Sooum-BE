package com.sooum.api.card.dto;

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
    private Integer cardLikeCnt;
    @JsonProperty(value = "isLiked")
    private Boolean isLiked;

    @Builder
    public CardSummary(int commentCnt, Integer cardLikeCnt, Boolean isLiked) {
        this.commentCnt = commentCnt;
        this.cardLikeCnt = cardLikeCnt;
        this.isLiked = isLiked;
    }
}