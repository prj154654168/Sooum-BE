package com.sooum.data.img.service;

import com.sooum.data.card.entity.Card;
import com.sooum.data.card.entity.CommentCard;
import com.sooum.data.card.entity.FeedCard;
import com.sooum.data.img.entity.CardImg;
import com.sooum.data.img.repository.CardImgRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CardImgService {
    private final CardImgRepository cardImgRepository;

    public void updateCardImg(Card card, String imgName) {
        Optional<CardImg> optionalCardImg = cardImgRepository.findByImgName(imgName);

        if(optionalCardImg.isPresent()) {
            CardImg cardImg = optionalCardImg.get();
            if (card instanceof FeedCard feedCard) {
                cardImg.updateFeedCard(feedCard);
            } else if (card instanceof CommentCard commentCard) {
                cardImg.updateCommentCard(commentCard);
            }
        }else throw new EntityNotFoundException();
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

    public void updateCardImgNull(Long memberPk){
        cardImgRepository.updateFeedCardImgNull(memberPk);
        cardImgRepository.updateCommentCardImgNull(memberPk);
    }
}