package com.sooum.core.domain.img.service;

import com.sooum.core.domain.card.entity.imgtype.ImgType;
import com.sooum.core.domain.img.dto.ImgUrlInfo;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface ImgService {
    Link findImgUrl(ImgType imgType, String imgName);
    List<ImgUrlInfo> createDefaultImgRetrieveUrls(Optional<List<String>> previousImgsName);
    List<String> findIssuedDefaultImgsName(List<ImgUrlInfo> imgsUrlInfo);
    ImgUrlInfo createUserUploadUrl(String extension);
    boolean verifyImgSaved(String imgName);
}
