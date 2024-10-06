package com.sooum.core.domain.card.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sooum.core.domain.card.entity.font.Font;
import com.sooum.core.domain.card.entity.fontsize.FontSize;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;

@Setter
@Getter
public abstract class FeedCardDto extends RepresentationModel<FeedCardDto> {
    private long id;
    @JsonProperty(value = "isStory")
    private boolean isStory;
    private LocalDateTime storyExpirationTime;
    private String content;
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

    public FeedCardDto(long id, boolean isStory, LocalDateTime storyExpirationTime, String content, LocalDateTime createdAt, int likeCnt, boolean isLiked, int commentCnt, boolean isCommentWritten, Link backgroundImgUrl, Font font, FontSize fontSize) {
        this.id = id;
        this.isStory = isStory;
        this.storyExpirationTime = storyExpirationTime;
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
}
