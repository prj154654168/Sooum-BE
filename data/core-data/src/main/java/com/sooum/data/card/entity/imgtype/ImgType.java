package com.sooum.data.card.entity.imgtype;

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
