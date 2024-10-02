package com.sooum.core.domain.img.service;

import com.sooum.core.domain.card.entity.imgtype.ImgType;
import com.sooum.core.domain.img.dto.ImgUrlInfo;
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
import java.util.Optional;
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
    public List<ImgUrlInfo> createDefaultImgRetrieveUrls(Optional<List<String>> previousImgsName) {
        List<Integer> allImgs = IntStream.rangeClosed(1, defaultImgSize)
                .boxed()
                .collect(Collectors.toList());
        Collections.shuffle(allImgs);

        if (previousImgsName.isPresent()) {
            List<Integer> originalPreviousImgsName = previousImgsName.get().stream()
                    .map(img -> Integer.parseInt(img.split("[.]")[0]))
                    .toList();

            allImgs.removeAll(originalPreviousImgsName);
        }

        return allImgs.subList(0, DEFAULT_IMG_CNT).stream()
                .map(img -> {
                    String imgName = img + "." + DEFAULT_IMG_EXTENSION;
                    return ImgUrlInfo.builder()
                            .imgName(imgName)
                            .url(findImgUrl(ImgType.DEFAULT, imgName))
                            .build();
                })
                .toList();
    }

    @Override
    public List<String> findIssuedDefaultImgsName(List<ImgUrlInfo> imgsUrlInfo) {
        return imgsUrlInfo.stream().map(ImgUrlInfo::getImgName).toList();
    }

    @Override
    public ImgUrlInfo createUserUploadUrl(String extension) {
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

    // todo s3로 storage 변경 시 삭제
    public void saveUserImg(MultipartFile file, String imgName) {
        Path filePath = Paths.get(serverImgPath + USER_IMG_PATH, imgName);
        try {
            file.transferTo(filePath);
        } catch (IOException e) {
            throw new RuntimeException("파일이 저장되지 않않습니다.");
        }
    }
}
