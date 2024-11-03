package com.sooum.data.img.service;

import com.sooum.data.card.entity.Card;
import com.sooum.data.img.entity.UserUploadPic;
import com.sooum.data.img.repository.ImgRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserImgService {
    private final ImgRepository imgRepository;

    public void saveUserUploadPic(Card card, String imgName) {
        UserUploadPic userUploadPic = UserUploadPic.builder()
                .imgName(imgName)
                .card(card).build();
        imgRepository.save(userUploadPic);
    }

    public void deleteUserUploadPic(String imgName) {
        imgRepository.deleteByImgName(imgName);
    }
}