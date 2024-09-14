package com.sooum.core.domain.card.entity.imgtype;

import lombok.Getter;

@Getter
public enum ImgType {
    DEFAULT("default"),
    USER("user");

    private final String imgPath;

    ImgType(String imgPath) {
        this.imgPath = imgPath;
    }
}
