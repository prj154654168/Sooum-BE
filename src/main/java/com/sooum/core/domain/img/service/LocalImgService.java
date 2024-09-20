package com.sooum.core.domain.img.service;

import com.sooum.core.domain.card.entity.imgtype.ImgType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;

@Service
public class LocalImgService implements ImgService{
    @Value("${sooum.server.ip}")
    private String serverIp;
    @Value("${sooum.server.img.path}")
    private String serverImgPath;
    private static final String DEFAULT_IMG_PATH = "/backgroundPic";
    private static final String USER_IMG_PATH = "/userUploadPic";

    @Override
    public String findImgUrl(ImgType imgType, String imgName) {
        return serverIp + "/imgs/" + imgName + "/" + imgType.getImgPath();
    }

    public UrlResource findImg(ImgType imgType, String imgName) throws MalformedURLException {
        String imgPath = imgType.equals(ImgType.DEFAULT) ? DEFAULT_IMG_PATH : USER_IMG_PATH;
        return new UrlResource("file:" + serverImgPath + "/" + imgPath + "/" + imgName + ".jpg");
    }
}
