package com.sooum.core.domain.card.dto;

import com.sooum.core.domain.card.entity.font.Font;
import com.sooum.core.domain.card.entity.fontsize.FontSize;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.Link;

import java.time.LocalDateTime;

@Getter
@Setter
public class PopularCardRetrieve extends CardDto {
    private Double distance;

    @Builder
    public PopularCardRetrieve(long id, boolean isStory, String content, LocalDateTime createdAt, int likeCnt, boolean isLiked, int commentCnt, boolean isCommentWritten, Link backgroundImgUrl, Font font, FontSize fontSize, Double distance) {
        super(id, isStory, content, createdAt, likeCnt, isLiked, commentCnt, isCommentWritten, backgroundImgUrl, font, fontSize);
        this.distance = distance;
    }
}
