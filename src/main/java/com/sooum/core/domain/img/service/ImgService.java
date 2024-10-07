package com.sooum.core.domain.img.service;

import com.sooum.core.domain.card.entity.imgtype.ImgType;
import com.sooum.core.domain.img.dto.ImgUrlInfo;
import org.springframework.hateoas.Link;

import java.util.List;

public interface ImgService {
    Link findImgUrl(ImgType imgType, String imgName);
    List<ImgUrlInfo> createDefaultImgRetrieveUrls(List<String> previousImgsName);
    String findIssuedDefaultImgsName(List<ImgUrlInfo> imgsUrlInfo);
    ImgUrlInfo createUserUploadUrl(String extension);
    boolean verifyImgSaved(String imgName);
    default boolean isModeratingImg(String imgName){
        return false;
    }
}
