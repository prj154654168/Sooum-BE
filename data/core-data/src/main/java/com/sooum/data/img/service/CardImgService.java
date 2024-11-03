package com.sooum.data.img.service;

import com.sooum.data.card.entity.Card;
import com.sooum.data.img.entity.CardImg;
import com.sooum.data.img.repository.CardImgRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CardImgService {
    private final CardImgRepository cardImgRepository;

    public void saveCardImg(Card card, String imgName) {
        CardImg cardImg = CardImg.builder()
                .imgName(imgName)
                .card(card).build();
        cardImgRepository.save(cardImg);
    }

    public void saveDefaultCardImg(String imgName) {
        cardImgRepository.save(new CardImg(imgName));
    }

    public void deleteUserUploadPic(String imgName) {
        cardImgRepository.deleteByImgName(imgName);
    }

    public void deleteCardImgs(List<CardImg> imgsName) {
        cardImgRepository.deleteAllInBatch(imgsName);
    }
}