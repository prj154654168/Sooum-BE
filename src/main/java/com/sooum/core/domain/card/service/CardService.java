package com.sooum.core.domain.card.service;

import com.sooum.core.domain.card.dto.CardSummary;
import com.sooum.core.domain.card.entity.parenttype.CardType;
import com.sooum.core.global.exceptionmessage.ExceptionMessage;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CardService {
    private final FeedLikeService feedLikeService;
    private final CommentLikeService commentLikeService;
    private final CommentCardService commentCardService;
    private final FeedCardService feedCardService;

    public CardSummary findCardSummary(Long parentCardPk, Long memberPk) {
        CardType parentCardType = commentCardService.findCardType(parentCardPk);

        return CardSummary.builder()
                .cardLikeCnt(findLikeCnt(parentCardPk, parentCardType))
                .isLiked(isLiked(parentCardPk, parentCardType, memberPk))
                .commentCnt(commentCardService.countComment(parentCardPk))
                .build();
    }

    public int findLikeCnt(Long cardPk, CardType cardType) {
        return cardType.equals(CardType.FEED_CARD)
                ? feedLikeService.countLike(cardPk)
                : commentLikeService.countLike(cardPk);
    }

    public boolean isLiked(Long cardPk, CardType cardType, Long memberPk) {
        return cardType.equals(CardType.FEED_CARD)
                ? feedLikeService.isLiked(cardPk, memberPk)
                : commentLikeService.isLiked(cardPk, memberPk);
    }

    @Transactional
    public void createCardLike(Long cardPk, Long memberPk) {
        CardType cardType = findCardType(cardPk);

        switch (cardType) {
            case FEED_CARD -> feedLikeService.createFeedLike(cardPk, memberPk);
            case COMMENT_CARD -> commentLikeService.createCommentLike(cardPk, memberPk);
            default -> throw new EntityNotFoundException(ExceptionMessage.CARD_NOT_FOUND.getMessage());
        }
    }

    @Transactional
    public void deleteCardLike(Long likedCardPk, Long likedMemberPk) {
        CardType cardType = findCardType(likedCardPk);

        switch (cardType) {
            case FEED_CARD -> feedLikeService.deleteFeedLike(likedCardPk, likedMemberPk);
            case COMMENT_CARD -> commentLikeService.deleteCommentLike(likedCardPk, likedMemberPk);
            default -> throw new EntityNotFoundException(ExceptionMessage.CARD_NOT_FOUND.getMessage());
        }
    }

    public CardType findCardType(Long cardPk) {
        return feedCardService.isExistFeedCard(cardPk) ? CardType.FEED_CARD : CardType.COMMENT_CARD;
    }
}
