package com.sooum.core.domain.card.service;

import com.sooum.core.domain.card.dto.CardSummary;
import com.sooum.core.domain.card.dto.MyFeedCardDto;
import com.sooum.core.domain.card.entity.parenttype.CardType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    public List<MyFeedCardDto> findMyCards(Long memberPk, Pageable pageable) {
        return feedCardService.findByMemberPk(memberPk, pageable);
    }
}
