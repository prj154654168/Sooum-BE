package com.sooum.core.domain.img.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.Link;


@Getter
@NoArgsConstructor
public class ImgUrlInfo {
    private String imgName;
    private Link url;

    @Builder
    public ImgUrlInfo(String imgName, Link url) {
        this.imgName = imgName;
        this.url = url;
    }
}
