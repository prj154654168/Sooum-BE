package com.sooum.api.img.service;

import com.sooum.api.img.dto.ImgUrlInfo;
import com.sooum.data.card.entity.imgtype.CardImgType;
import org.springframework.hateoas.Link;

import java.util.List;

public interface ImgService {
    Link findCardImgUrl(CardImgType imgType, String imgName);
    List<ImgUrlInfo> createDefaultImgRetrieveUrls(List<String> previousImgsName);
    String findIssuedDefaultImgsName(List<ImgUrlInfo> imgsUrlInfo);
    ImgUrlInfo createCardImgUploadUrl(String extension);
    ImgUrlInfo createProfileImgUploadUrl(String extension);
    boolean isCardImgSaved(String imgName);
    default boolean isProfileImgSaved(String profileImgName) {
        return false;
    }
    default boolean isModeratingImg(String imgName){
        return false;
    }
    default Link findProfileImgUrl(String imgName){return null;}
}
