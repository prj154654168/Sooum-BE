package com.sooum.data.card.service;

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
                .orElseThrow(() -> new EntityNotFoundException("카드를 찾을 수 없습니다."));
    }

    public Optional<FeedCard> findOptFeedCard(Long commentCardPk) {
        return feedCardRepository.findById(commentCardPk);
    }

    public boolean isExistFeedCard(Long feedCardPk) {
        return feedCardRepository.existsById(feedCardPk);
    }

    public FeedCard findByPk(Long feedCardPk) {
        return feedCardRepository.findById(feedCardPk)
                .orElseThrow(() -> new EntityNotFoundException("카드를 찾을 수 없습니다."));
    }

    public void saveFeedCard(FeedCard feedCard) {
        feedCardRepository.save(feedCard);
    }

    public Long findFeedCardCnt(Member cardOwnerMember) {
        return feedCardRepository.findFeedCardCnt(cardOwnerMember);
    }

    public List<FeedCard> findMemberFeedCards(Long memberPk, Long lastPk) {
        PageRequest pageRequest = PageRequest.ofSize(MAX_PAGE_SIZE);
        return feedCardRepository.findMemberFeedCards(memberPk, lastPk, pageRequest);
    }

    public List<FeedCard> findMyFeedCards(Long memberPk, Long lastPk) {
        PageRequest pageRequest = PageRequest.ofSize(MAX_PAGE_SIZE);
        return feedCardRepository.findMyFeedCards(memberPk, lastPk, pageRequest);
    }

    public void deleteFeedCardByMemberPk(Long memberPk) {
        feedCardRepository.deleteFeedCardByMemberPk(memberPk);
    }
}
