package com.sooum.core.domain.img.service;

import com.sooum.core.domain.card.entity.Card;
import com.sooum.core.domain.img.entity.UserUploadPic;
import com.sooum.core.domain.img.repository.ImgRepository;
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
}