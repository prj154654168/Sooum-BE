package com.sooum.api.img.service;

import com.sooum.api.img.dto.ImgUrlInfo;
import com.sooum.client.aws.rekognition.RekognitionService;
import com.sooum.client.aws.s3.S3ImgService;
import com.sooum.client.aws.s3.imgproperties.S3ImgPathProperties;
import com.sooum.data.card.entity.imgtype.CardImgType;
import com.sooum.data.img.service.CardImgService;
import com.sooum.data.img.service.ProfileImgService;
import com.sooum.global.exceptionmessage.ExceptionMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Primary
@Service
@RequiredArgsConstructor
public class AWSImgService implements ImgService{
    @Value("${sooum.server.img.default-size}")
    private int defaultImgSize;

    private final ProfileImgService profileImgService;
    private final CardImgService cardImgService;
    private final RekognitionService rekognitionService;
    private final S3ImgService s3Service;
    private final S3ImgPathProperties s3ImgPathProperties;

    private static final int DEFAULT_IMG_CNT = 8;
    private static final String DEFAULT_IMG_EXTENSION = "jpeg";

    /***
     *
     * @param imgName 확장자를 포함한 이미지 이름을 넣어주세요. Ex) dfjEiG-esi-2342j.jpeg
     * @return true인 경우 사용 불가능한 사진입니다.
     */
    @Override
    public boolean isModeratingCardImg(String imgName) {
        return rekognitionService.isModeratingImg(s3ImgPathProperties.getUSER_CARD_IMG_PATH(), imgName);
    }

    @Override
    public boolean isModeratingProfileImg(String imgName) {
        return rekognitionService.isModeratingImg(s3ImgPathProperties.getPROFILE_IMG_PATH(), imgName);
    }

    @Override
    public Link findCardImgUrl(CardImgType cardImgType, String imgName) {
        return switch (cardImgType) {
            case DEFAULT -> createGetPresignedLink(s3ImgPathProperties.getDEFAULT_CARD_IMG_PATH(), imgName);
            case USER -> createGetPresignedLink(s3ImgPathProperties.getUSER_CARD_IMG_PATH(), imgName);
        };
    }

    private Link createGetPresignedLink(String imgPathPrefix, String imgName) {
        return Link.of(s3Service.generateGetPresignedUrl(imgPathPrefix, imgName));
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
                        .url(findCardImgUrl(CardImgType.DEFAULT, imgName))
                        .build())
                .toList();
    }

    @Override
    public String findIssuedDefaultImgsName(List<ImgUrlInfo> imgsUrlInfo) {
        return imgsUrlInfo.stream().map(ImgUrlInfo::getImgName).collect(Collectors.joining(","));
    }

    @Override
    @Transactional
    public ImgUrlInfo createCardImgUploadUrl(String extension) {
        return createUploadUrlAndSaveImgEntity(s3ImgPathProperties.getUSER_CARD_IMG_PATH(), extension);
    }

    @Override
    @Transactional
    public ImgUrlInfo createProfileImgUploadUrl(String extension) {
        return createUploadUrlAndSaveImgEntity(s3ImgPathProperties.getPROFILE_IMG_PATH(), extension);
    }

    private ImgUrlInfo createUploadUrlAndSaveImgEntity(String filePath, String extension) {
        if (!extension.equalsIgnoreCase("jpeg")) {
            throw new UnsupportedOperationException(ExceptionMessage.UNSUPPORTED_IMAGE_FORMAT.getMessage());
        }
        String imgName = UUID.randomUUID() + "." + extension;

        if (filePath.equals(s3ImgPathProperties.getUSER_CARD_IMG_PATH())) {
            cardImgService.saveDefaultCardImg(imgName);
        }else if (filePath.equals(s3ImgPathProperties.getPROFILE_IMG_PATH())) {
            profileImgService.saveDefaultProfileImg(imgName);
        }else throw new IllegalArgumentException(ExceptionMessage.UNSUPPORTED_IMAGE_FORMAT.getMessage());

        return ImgUrlInfo.builder()
                .imgName(imgName)
                .url(Link.of(s3Service.generatePutPresignedUrl(filePath, imgName)))
                .build();
    }

    @Override
    public boolean isCardImgSaved(String imgName) {
        return s3Service.isImgSaved(s3ImgPathProperties.getUSER_CARD_IMG_PATH(), imgName);
    }

    @Override
    public boolean isProfileImgSaved(String profileImgName) {
        return s3Service.isImgSaved(s3ImgPathProperties.getPROFILE_IMG_PATH(), profileImgName);
    }

    @Override
    public Link findProfileImgUrl(String imgName) {
        if (imgName == null) {
            return null;
        }
        return createGetPresignedLink(s3ImgPathProperties.getPROFILE_IMG_PATH(), imgName);
    }
}
