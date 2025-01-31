package com.sooum.api.card.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sooum.data.card.entity.CommentCard;
import com.sooum.data.card.entity.FeedCard;
import com.sooum.data.card.entity.FeedLike;
import com.sooum.data.card.entity.font.Font;
import com.sooum.data.card.entity.fontsize.FontSize;
import com.sooum.global.util.CardUtils;
import com.sooum.global.util.DistanceUtils;
import lombok.Getter;
import lombok.Setter;
import org.locationtech.jts.geom.Point;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Setter
@Getter
public abstract class CardDto extends RepresentationModel<CardDto> {
    private String id;
    private String content;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
    private LocalDateTime createdAt;
    private int likeCnt;
    @JsonProperty(value = "isLiked")
    private boolean isLiked;
    private int commentCnt;
    @JsonProperty(value = "isCommentWritten")
    private boolean isCommentWritten;
    private Link backgroundImgUrl;
    private Font font;
    private FontSize fontSize;

    public CardDto(String id, String content, LocalDateTime createdAt, int likeCnt, boolean isLiked, int commentCnt, boolean isCommentWritten, Link backgroundImgUrl, Font font, FontSize fontSize) {
        this.id = id;
        this.content = content;
        this.createdAt = createdAt;
        this.likeCnt = likeCnt;
        this.isLiked = isLiked;
        this.commentCnt = commentCnt;
        this.isCommentWritten = isCommentWritten;
        this.backgroundImgUrl = backgroundImgUrl;
        this.font = font;
        this.fontSize = fontSize;
    }

    public CardDto(Long memberPk, FeedCard feedCard, List<CommentCard> commentCards, List<FeedLike> feedLikes, Link backgroundImgUrl) {
        this.id = feedCard.getPk().toString();
        this.content = feedCard.getContent();
        this.createdAt = feedCard.getCreatedAt();
        this.likeCnt = CardUtils.countLikes(feedCard, feedLikes);
        this.isLiked = CardUtils.isLiked(feedCard, feedLikes, memberPk);
        this.commentCnt = CardUtils.countComments(feedCard, commentCards);
        this.isCommentWritten = CardUtils.isWrittenCommentCard(feedCard, commentCards, memberPk);
        this.backgroundImgUrl = backgroundImgUrl;
        this.font = feedCard.getFont();
        this.fontSize = feedCard.getFontSize();
    }

    public Double getDistance(Point point, Optional<Double> latitude, Optional<Double> longitude){
        return DistanceUtils.calculate(point, latitude, longitude);
    }
}