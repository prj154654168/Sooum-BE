package com.sooum.api.card.dto;

import com.sooum.data.card.entity.font.Font;
import com.sooum.data.card.entity.fontsize.FontSize;
import lombok.Getter;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;

@Getter
public abstract class MyCardDto extends RepresentationModel<MyCardDto> {
    private String id;
    private String content;
    private Link backgroundImgUrl;
    private Font font;
    private FontSize fontSize;

    public MyCardDto(String id, String content, Link backgroundImgUrl, Font font, FontSize fontSize) {
        this.id = id;
        this.content = content;
        this.backgroundImgUrl = backgroundImgUrl;
        this.font = font;
        this.fontSize = fontSize;
    }
}
