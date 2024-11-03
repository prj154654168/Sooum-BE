package com.sooum.api.img.service;

import com.sooum.api.img.dto.ImgUrlInfo;
import com.sooum.data.card.entity.imgtype.ImgType;
import com.sooum.global.exceptionmessage.ExceptionMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.DetectModerationLabelsRequest;
import software.amazon.awssdk.services.rekognition.model.Image;
import software.amazon.awssdk.services.rekognition.model.ModerationLabel;
import software.amazon.awssdk.services.rekognition.model.S3Object;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
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
    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${sooum.server.img.default-size}")
    private int defaultImgSize;

    private final S3Presigner s3Presigner;
    private final S3Client s3Client;
    private final RekognitionClient rekognitionClient;

    private static final String DEFAULT_IMG = "card/default/";
    private static final String USER_IMG = "card/user/";
    private static final String PROFILE_IMG = "profile/";
    private static final Duration EXPIRY_TIME = Duration.ofMinutes(1L);
    private static final int DEFAULT_IMG_CNT = 8;
    private static final String DEFAULT_IMG_EXTENSION = "jpeg";

    /***
     *
     * @param imgName 확장자를 포함한 이미지 이름을 넣어주세요. Ex) dfjEiG-esi-2342j.jpeg
     * @return true인 경우 사용 불가능한 사진입니다.
     */
    @Override
    public boolean isModeratingImg(String imgName) {
        DetectModerationLabelsRequest build = DetectModerationLabelsRequest.builder()
                .minConfidence(70F)
                .image(Image.builder()
                        .s3Object(S3Object.builder()
                                .bucket(bucket)
                                .name(USER_IMG + imgName)
                                .build())
                        .build())
                .build();

        List<ModerationLabel> moderationLabels = rekognitionClient.detectModerationLabels(build).moderationLabels();
        for (ModerationLabel label : moderationLabels) {
            if (isExplicitNudity(label) || isExplicitSexualActivity(label) || isSexToys(label)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isSexToys(ModerationLabel label) {
        return label.name().equalsIgnoreCase("Sex Toys") && label.taxonomyLevel().equals(2);
    }

    private static boolean isExplicitSexualActivity(ModerationLabel label) {
        return label.name().equalsIgnoreCase("Explicit Sexual Activity") && label.taxonomyLevel().equals(2);
    }

    private static boolean isExplicitNudity(ModerationLabel label) {
        return label.name().equalsIgnoreCase("Explicit Nudity") && label.taxonomyLevel().equals(2);
    }

    @Override
    public Link findImgUrl(ImgType imgType, String imgName) {
        return switch (imgType) {
            case DEFAULT -> createGetPresignedLink(DEFAULT_IMG, imgName);
            case USER -> createGetPresignedLink(USER_IMG, imgName);
        };
    }

    private Link createGetPresignedLink(String imgPathPrefix, String imgName) {
        return Link.of(s3Presigner.presignGetObject(
                        GetObjectPresignRequest.builder()
                                .getObjectRequest(GetObjectRequest.builder()
                                        .bucket(bucket)
                                        .key(imgPathPrefix + imgName)
                                        .build())
                                .signatureDuration(EXPIRY_TIME)
                                .build())
                .url()
                .toString());
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
        return createUploadUrl(USER_IMG, extension);
    }

    @Override
    public ImgUrlInfo createProfileImgUploadUrl(String extension) {
        return createUploadUrl(PROFILE_IMG, extension);
    }

    private ImgUrlInfo createUploadUrl(String filePath, String extension) {
        if (!extension.equalsIgnoreCase("jpeg")) {
            throw new UnsupportedOperationException(ExceptionMessage.UNSUPPORTED_IMAGE_FORMAT.getMessage());
        }
        String imgName = UUID.randomUUID() + "." + extension;
        return ImgUrlInfo.builder()
                .imgName(imgName)
                .url(Link.of(s3Presigner.presignPutObject(PutObjectPresignRequest.builder()
                                .putObjectRequest(PutObjectRequest.builder()
                                        .bucket(bucket)
                                        .key(filePath + imgName)
                                        .build())
                                .signatureDuration(EXPIRY_TIME)
                                .build())
                        .url().toString()))
                .build();
    }

    @Override
    public boolean verifyImgSaved(String imgName) {
        try {
            s3Client.headObject(HeadObjectRequest.builder()
                    .bucket(bucket)
                    .key(USER_IMG + imgName)
                    .build());
        } catch (Exception e) {
            log.error("{}",e.getMessage());
            return false;
        } finally {
            s3Client.close();
        }
        return true;
    }

    @Override
    public Link findProfileImgUrl(String imgName) {
        if (imgName == null) {
            return null;
        }
        return createGetPresignedLink(PROFILE_IMG, imgName);
    }
}
