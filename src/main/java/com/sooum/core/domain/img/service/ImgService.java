package com.sooum.core.domain.img.service;

import com.sooum.core.domain.card.entity.imgtype.ImgType;
import org.springframework.stereotype.Service;

@Service
public interface ImgService {
    String findImgUrl(ImgType imgType, String imgName);
}
