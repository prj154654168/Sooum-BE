package com.sooum.core.domain.card.service;

import com.sooum.core.domain.card.entity.FeedCard;
import com.sooum.core.domain.card.entity.FeedLike;
import com.sooum.core.domain.card.entity.parenttype.CardType;
import com.sooum.core.domain.card.repository.FeedLikeRepository;
import com.sooum.core.domain.member.entity.Member;
import com.sooum.core.domain.member.service.MemberService;
import com.sooum.core.global.exceptionmessage.ExceptionMessage;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FeedLikeService {
    private final MemberService memberService;
    private final FeedCardService feedCardService;
    private final FeedLikeRepository feedLikeRepository;
    private final CommentLikeService commentLikeService;

    public List<FeedLike> findByTargetCards(List<FeedCard> targetCards) {
        return feedLikeRepository.findByTargetList(targetCards);
    }

    public void deleteAllFeedLikes(Long feedCardPk) {
        feedLikeRepository.deleteAllFeedLikes(feedCardPk);
    }

    public boolean hasLiked(Long feedCardPk, Long memberPk) {
        return feedLikeRepository.existsByTargetCardPkAndLikedMemberPk(feedCardPk, memberPk);
    }

    public int countLike(Long feedCardPk) {
        return feedLikeRepository.countByTargetCard_Pk(feedCardPk);
    }

    @Transactional
    public void createFeedLike(Long targetFeedCardPk, Long requesterPk) {
        if (feedLikeRepository.existsByTargetCardPkAndLikedMemberPk(targetFeedCardPk, requesterPk)) {
            throw new EntityExistsException(ExceptionMessage.ALREADY_CARD_LIKED.getMessage());
        }

        Member likedMember = memberService.findByPk(requesterPk);
        FeedCard targetCard = feedCardService.findByPk(targetFeedCardPk);
        feedLikeRepository.save(
                FeedLike.builder()
                        .likedMember(likedMember)
                        .targetCard(targetCard)
                        .build()
        );
    }

    @Transactional
    public void deleteFeedLike(Long likedFeedCardPk, Long likedMemberPk) {
        FeedLike feedLiked = feedLikeRepository.findFeedLiked(likedFeedCardPk, likedMemberPk)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.LIKE_NOT_FOUND.getMessage()));
        feedLikeRepository.delete(feedLiked);
    }

    @Transactional
    public void createCardLike(Long cardPk, Long memberPk) {
        CardType cardType = findCardType(cardPk);

        switch (cardType) {
            case FEED_CARD -> createFeedLike(cardPk, memberPk);
            case COMMENT_CARD -> commentLikeService.createCommentLike(cardPk, memberPk);
            default -> throw new EntityNotFoundException(ExceptionMessage.CARD_NOT_FOUND.getMessage());
        }
    }

    @Transactional
    public void deleteCardLike(Long likedCardPk, Long likedMemberPk) {
        CardType cardType = findCardType(likedCardPk);

        switch (cardType) {
            case FEED_CARD -> deleteFeedLike(likedCardPk, likedMemberPk);
            case COMMENT_CARD -> commentLikeService.deleteCommentLike(likedCardPk, likedMemberPk);
            default -> throw new EntityNotFoundException(ExceptionMessage.CARD_NOT_FOUND.getMessage());
        }
    }

    public CardType findCardType(Long cardPk) {
        return feedCardService.isExistFeedCard(cardPk) ? CardType.FEED_CARD : CardType.COMMENT_CARD;
    }

    public boolean isLiked(Long cardPk, Long memberPk){
            return feedLikeRepository.existsByTargetCardPkAndLikedMemberPk(cardPk, memberPk);
    }
}
