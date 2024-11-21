package com.sooum.data.card.service;

import com.sooum.data.card.entity.FeedCard;
import com.sooum.data.card.entity.FeedLike;
import com.sooum.data.card.repository.FeedLikeRepository;
import com.sooum.data.member.entity.Member;
import com.sooum.data.member.service.MemberService;
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
                throw new EntityExistsException();
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
                .orElseThrow(() -> new EntityNotFoundException("좋아요 이력을 찾을 수 않습니다."));

        if (feedLiked.isDeleted()) {
            throw new EntityExistsException();
        }

        feedLiked.delete();
    }

    public boolean isLiked(Long cardPk, Long memberPk){
        return feedLikeRepository.findExistFeedLike(cardPk, memberPk).isPresent();
    }

    public void deleteAllMemberLikes(Long memberPk){
        feedLikeRepository.deleteAllMemberLikes(memberPk);
    }
}
