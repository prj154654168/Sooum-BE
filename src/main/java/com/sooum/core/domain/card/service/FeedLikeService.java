package com.sooum.core.domain.card.service;

import com.sooum.core.domain.card.entity.FeedCard;
import com.sooum.core.domain.card.entity.FeedLike;
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
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FeedLikeService {
    private final MemberService memberService;
    private final FeedCardService feedCardService;
    private final FeedLikeRepository feedLikeRepository;

    public List<FeedLike> findByTargetCards(List<FeedCard> targetCards) {
        return feedLikeRepository.findByTargetList(targetCards);
    }

    public void deleteAllFeedLikes(Long feedCardPk) {
        feedLikeRepository.deleteAllFeedLikes(feedCardPk);
    }

    public int countLike(Long feedCardPk) {
        return feedLikeRepository.countByTargetCard_Pk(feedCardPk);
    }

    @Transactional
    public void createFeedLike(Long targetFeedCardPk, Long requesterPk) {
        Optional<FeedLike> findFeedLiked = feedLikeRepository.findFeedLiked(targetFeedCardPk, requesterPk);
        if (findFeedLiked.isPresent()) {
            if (!findFeedLiked.get().isDeleted()) {
                throw new EntityExistsException(ExceptionMessage.ALREADY_CARD_LIKED.getMessage());
            }

            findFeedLiked.get().create();
            return;
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

        if (feedLiked.isDeleted()) {
            throw new EntityExistsException(ExceptionMessage.ALREADY_DELETE_CARD_LIKE.getMessage());
        }

        feedLiked.delete();
    }

    public boolean isLiked(Long cardPk, Long memberPk){
            return feedLikeRepository.existsByTargetCardPkAndLikedMemberPk(cardPk, memberPk);
    }
}
