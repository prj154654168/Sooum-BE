package com.sooum.api.card.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sooum.data.card.entity.font.Font;
import com.sooum.data.card.entity.imgtype.ImgType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public abstract class CreateCardDto extends RepresentationModel<CreateCardDto> {
    @JsonProperty(value = "isDistanceShared")
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
