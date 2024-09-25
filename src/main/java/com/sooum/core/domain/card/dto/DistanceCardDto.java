package com.sooum.core.domain.card.dto;

import com.sooum.core.domain.card.entity.font.Font;
import com.sooum.core.domain.card.entity.fontsize.FontSize;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.hateoas.Link;

import java.time.LocalDateTime;

@Getter
public class DistanceCardDto extends CardDto {

    private Double distance;

    @Builder
    public DistanceCardDto(long id, boolean isStory, String content, LocalDateTime createdAt, int likeCnt, boolean isLiked, int commentCnt, boolean isCommentWritten, Link backgroundImgUrl, Font font, FontSize fontSize, @NonNull Double distance) {
        super(id, isStory, content, createdAt, likeCnt, isLiked, commentCnt, isCommentWritten, backgroundImgUrl, font, fontSize);
        this.distance = distance;
    }
}
