package com.sooum.core.domain.card.dto;

import com.sooum.core.domain.card.entity.font.Font;
import com.sooum.core.domain.card.entity.imgtype.ImgType;
import lombok.Getter;
import org.springframework.hateoas.RepresentationModel;

import java.util.List;

@Getter
public abstract class CreateCardDto extends RepresentationModel<CreateCardDto> {
    private boolean isDistanceShared;
    private double latitude;
    private double longitude;
    private String content;
    private Font font;
    private ImgType imgType;
    private String imgName;

    public CreateCardDto(String content, boolean isDistanceShared, double latitude, double longitude, Font font, ImgType imgType, String imgName) {
        this.content = content;
        this.isDistanceShared = isDistanceShared;
        this.latitude = latitude;
        this.longitude = longitude;
        this.font = font;
        this.imgType = imgType;
        this.imgName = imgName;
    }

    public abstract List<String> getTags();
}
