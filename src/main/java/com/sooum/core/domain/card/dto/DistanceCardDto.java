package com.sooum.core.domain.card.dto;

import com.sooum.core.domain.card.entity.font.Font;
import com.sooum.core.domain.card.entity.fontsize.FontSize;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.hateoas.Link;

import java.time.LocalDateTime;

@Getter
@Setter
public class DistanceCardDto extends CardDto {
    private LocalDateTime storyExpirationTime;
    private Double distance;

    @Builder
    public DistanceCardDto(String id, boolean isStory, String content, LocalDateTime createdAt, int likeCnt, boolean isLiked, int commentCnt, boolean isCommentWritten, Link backgroundImgUrl, Font font, FontSize fontSize, @NonNull Double distance) {
        super(id, content, createdAt, likeCnt, isLiked, commentCnt, isCommentWritten, backgroundImgUrl, font, fontSize);
        this.storyExpirationTime = isStory ? createdAt.plusDays(1L) : null;
        this.distance = distance;
    }
}
