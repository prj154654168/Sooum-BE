package com.sooum.api.card.dto;

import com.sooum.data.card.entity.font.Font;
import com.sooum.data.card.entity.fontsize.FontSize;
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
