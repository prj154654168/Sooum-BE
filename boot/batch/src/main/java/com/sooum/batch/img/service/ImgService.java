package com.sooum.batch.img.service;

import com.sooum.client.aws.s3.S3ImgService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ImgService {
    private final S3ImgService s3Service;
    private static final String USER_CARD_IMG = "card/user/";
    private static final String PROFILE_IMG = "profile/";

    public void deleteProfileImgs(List<String> imgsName) {
        deleteImgs(PROFILE_IMG, imgsName);
    }

    public void deleteCardImgs(List<String> imgsName) {
        deleteImgs(USER_CARD_IMG, imgsName);
    }

    public void deleteImgs(String filePath, List<String> imgsName) {
        s3Service.deleteImgs(filePath, imgsName);
    }
}
