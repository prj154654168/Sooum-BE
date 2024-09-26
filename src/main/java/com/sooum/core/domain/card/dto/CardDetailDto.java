package com.sooum.core.domain.card.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sooum.core.domain.card.entity.font.Font;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;

@Setter
@Getter
public abstract class CardDetailDto extends RepresentationModel<CardDetailDto> {
    private long id;
    private String content;
    private Link backgroundImgUrl;
    private Font font;
    private Double distance;
    private LocalDateTime createdAt;
    @JsonProperty(value = "isLiked")
    private boolean isLiked;
    private int likeCnt;
    @JsonProperty(value = "isOwnCard")
    private boolean isOwnCard;


    public CardDetailDto(long id, String content, Link backgroundImgUrl, Font font, Double distance, LocalDateTime createdAt, boolean isLiked, int likeCnt, boolean isOwnCard) {
        this.id = id;
        this.content = content;
        this.backgroundImgUrl = backgroundImgUrl;
        this.font = font;
        this.distance = distance;
        this.createdAt = createdAt;
        this.isLiked = isLiked;
        this.likeCnt = likeCnt;
        this.isOwnCard = isOwnCard;
    }
}
