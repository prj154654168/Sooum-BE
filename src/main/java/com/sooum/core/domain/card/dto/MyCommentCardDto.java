package com.sooum.core.domain.card.dto;

import com.sooum.core.domain.card.entity.font.Font;
import com.sooum.core.domain.card.entity.fontsize.FontSize;
import lombok.Builder;
import lombok.Getter;
import org.springframework.hateoas.Link;

@Getter
public class MyCommentCardDto extends MyCardDto {

    @Builder
    public MyCommentCardDto(String id, String content, Link backgroundImgUrl, Font font, FontSize fontSize) {
        super(id, content, backgroundImgUrl, font, fontSize);
    }
}
