package com.sooum.api.img.service;

import com.sooum.api.img.dto.ImgUrlInfo;
import com.sooum.data.card.entity.imgtype.ImgType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class LocalImgService implements ImgService{
    @Value("${sooum.server.ip}")
    private String serverIp;
    @Value("${sooum.server.img.path}")
    private String serverImgPath;
    @Value("${sooum.server.img.default-size}")
    private int defaultImgSize;
    private static final String IMG_URL_PREFIX = "/imgs";
    private static final String DEFAULT_IMG_PATH = "/backgroundPic";
    private static final String USER_IMG_PATH = "/userUploadPic";
    private static final int DEFAULT_IMG_CNT = 8;
    private static final String DEFAULT_IMG_EXTENSION = "png";

    @Override
    public Link findImgUrl(ImgType imgType, String imgName) {
        return Link.of(serverIp + IMG_URL_PREFIX + "/" + imgName + "/" + imgType.getImgPath());
    }

    @Override
    public List<ImgUrlInfo> createDefaultImgRetrieveUrls(List<String> previousImgsName) {
        List<String> allDefaultImgsName = IntStream.rangeClosed(1, defaultImgSize)
                .boxed()
                .map(img -> img + "." + DEFAULT_IMG_EXTENSION)
                .collect(Collectors.toList());
        Collections.shuffle(allDefaultImgsName);

        if (!previousImgsName.isEmpty()) {
            allDefaultImgsName.removeAll(previousImgsName);
        }

        return allDefaultImgsName.subList(0, DEFAULT_IMG_CNT).stream()
                .map(imgName -> ImgUrlInfo.builder()
                        .imgName(imgName)
                        .url(findImgUrl(ImgType.DEFAULT, imgName))
                        .build())
                .toList();
    }

    @Override
    public String findIssuedDefaultImgsName(List<ImgUrlInfo> imgsUrlInfo) {
        return imgsUrlInfo.stream().map(ImgUrlInfo::getImgName).collect(Collectors.joining(","));
    }

    @Override
    public ImgUrlInfo createCardImgUploadUrl(String extension) {
        String imgName = createRandomImgName(extension);
        return ImgUrlInfo.builder()
                .imgName(imgName)
                .url(findImgUrl(ImgType.USER, imgName))
                .build();
    }

    @Override
    public ImgUrlInfo createProfileImgUploadUrl(String extension) {
        String imgName = createRandomImgName(extension);
        return ImgUrlInfo.builder()
                .imgName(imgName)
                .url(findImgUrl(ImgType.USER, imgName))
                .build();
    }

    private static String createRandomImgName(String extension) {
        return UUID.randomUUID() + "." + extension;
    }

    @Override
    public boolean verifyImgSaved(String imgName) {
        Path filePath = Paths.get(serverImgPath + USER_IMG_PATH, imgName);
        return Files.exists(filePath);
    }

    public UrlResource findImg(ImgType imgType, String imgName) throws MalformedURLException {
        String imgPath = imgType.equals(ImgType.DEFAULT) ? DEFAULT_IMG_PATH : USER_IMG_PATH;
        return new UrlResource("file:" + serverImgPath + imgPath + "/" + imgName);
    }

    public void saveUserImg(MultipartFile file, String imgName) {
        Path filePath = Paths.get(serverImgPath + USER_IMG_PATH, imgName);
        try {
            file.transferTo(filePath);
        } catch (IOException e) {
            throw new RuntimeException("파일이 저장되지 않않습니다.");
        }
    }
}
