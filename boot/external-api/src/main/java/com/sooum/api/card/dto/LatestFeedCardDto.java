package com.sooum.api.card.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sooum.data.card.entity.font.Font;
import com.sooum.data.card.entity.fontsize.FontSize;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.Link;

import java.time.LocalDateTime;
@Getter
@Setter
public class LatestFeedCardDto extends CardDto {
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
    private LocalDateTime storyExpirationTime;
    private final Double distance;
    @Builder
    public LatestFeedCardDto(String id, boolean isStory, String content, LocalDateTime createdAt, int likeCnt, boolean isLiked, int commentCnt, boolean isCommentWritten, Link backgroundImgUrl, Font font, FontSize fontSize, Double distance) {
        super(id, content, createdAt, likeCnt, isLiked, commentCnt, isCommentWritten, backgroundImgUrl, font, fontSize);
        this.storyExpirationTime = isStory ? createdAt.plusDays(1L) : null;
        this.distance = distance;
    }
}
