package com.sooum.core.domain.card.dto;

import com.sooum.core.domain.card.entity.font.Font;
import com.sooum.core.domain.card.entity.fontsize.FontSize;
import lombok.Builder;
import lombok.Getter;
import org.springframework.hateoas.Link;

import java.time.LocalDateTime;
@Getter
public class LatestFeedCardDto extends FeedCardDto{

    private final Double distance;
    @Builder
    public LatestFeedCardDto(long id, boolean isStory, LocalDateTime storyExpirationTime, String content, LocalDateTime createdAt, int likeCnt, boolean isLiked, int commentCnt, boolean isCommentWritten, Link backgroundImgUrl, Font font, FontSize fontSize, Double distance) {
        super(id, isStory, storyExpirationTime, content, createdAt, likeCnt, isLiked, commentCnt, isCommentWritten, backgroundImgUrl, font, fontSize);
        this.distance = distance;
    }
}
