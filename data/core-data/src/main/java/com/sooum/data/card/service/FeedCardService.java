package com.sooum.data.card.service;

import com.sooum.data.card.entity.CommentCard;
import com.sooum.data.card.entity.FeedCard;
import com.sooum.data.card.repository.FeedCardRepository;
import com.sooum.data.member.entity.Member;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Point;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FeedCardService {
    private final FeedCardRepository feedCardRepository;
    private static final int MAX_PAGE_SIZE = 50;

    public List<FeedCard> findByLastId(Optional<Long> lastId, List<Long> blockMemberPkList) {
        Pageable pageRequest = PageRequest.ofSize(MAX_PAGE_SIZE);
        return feedCardRepository.findByNextPage(lastId.orElse(null), blockMemberPkList, pageRequest);
    }

    public List<FeedCard> findFeedsByDistance (Optional<Long> lastPk, Point userLocation, double minDist, double maxDist, List<Long> blockMemberPks) {
        Pageable pageRequest = PageRequest.ofSize(MAX_PAGE_SIZE);
        return feedCardRepository.findNextByDistance(lastPk.orElse(null), userLocation, minDist, maxDist, blockMemberPks, pageRequest);
    }

    public void deleteFeedCard(Long feedCardPk) {
        feedCardRepository.deleteById(feedCardPk);
    }

    public FeedCard findFeedCard(Long feedCardPk) {
        return feedCardRepository.findById(feedCardPk)
                .orElseThrow(EntityNotFoundException::new);
    }

    public Optional<FeedCard> findOptFeedCard(Long commentCardPk) {
        return feedCardRepository.findById(commentCardPk);
    }

    public boolean isExistFeedCard(Long feedCardPk) {
        return feedCardRepository.existsById(feedCardPk);
    }

    public FeedCard findByPk(Long feedCardPk) {
        return feedCardRepository.findById(feedCardPk)
                .orElseThrow(EntityNotFoundException::new);
    }

    public void saveFeedCard(FeedCard feedCard) {
        feedCardRepository.save(feedCard);
    }

    public Long findFeedCardCnt(Member cardOwnerMember) {
        return feedCardRepository.findFeedCardCnt(cardOwnerMember);
    }

    public List<Long> findFeedCardIdsByMemberPk(List<Long> memberPks) {
        return feedCardRepository.findFeedCardIdsByWriterIn(memberPks);
    }

    public List<FeedCard> findFeedList(Long memberPk, Optional<Long> lastPk) {
        PageRequest pageRequest = PageRequest.ofSize(30);
        return lastPk.isEmpty()
                ? feedCardRepository.findCommentCardsFirstPage(memberPk, pageRequest)
                : feedCardRepository.findCommentCardsNextPage(memberPk, lastPk.get(), pageRequest);
    }

    public void clearWriterByMemberPk(Long memberPk) {
        feedCardRepository.clearWriterByMemberPk(memberPk);
    }
}
