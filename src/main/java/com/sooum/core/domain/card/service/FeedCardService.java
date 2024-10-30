package com.sooum.core.domain.card.service;

import com.sooum.core.domain.card.dto.MyFeedCardDto;
import com.sooum.core.domain.card.entity.FeedCard;
import com.sooum.core.domain.card.repository.FeedCardRepository;
import com.sooum.core.domain.img.service.ImgService;
import com.sooum.core.domain.member.entity.Member;
import com.sooum.core.global.exceptionmessage.ExceptionMessage;
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
    private final ImgService imgService;
    private final FeedCardRepository feedCardRepository;
    private static final int MAX_PAGE_SIZE = 50;

    List<FeedCard> findByLastId(Optional<Long> lastId, List<Long> blockMemberPkList) {
        Pageable pageRequest = PageRequest.ofSize(MAX_PAGE_SIZE);
        return feedCardRepository.findByNextPage(lastId.orElse(null), blockMemberPkList, pageRequest);
    }

    List<FeedCard> findFeedsByDistance (Optional<Long> lastPk, Point userLocation, double minDist, double maxDist, List<Long> blockMemberPks) {
        Pageable pageRequest = PageRequest.ofSize(MAX_PAGE_SIZE);
        return feedCardRepository.findNextByDistance(lastPk.orElse(null), userLocation, minDist, maxDist, blockMemberPks, pageRequest);
    }

    public void deleteFeedCard(Long feedCardPk) {
        feedCardRepository.deleteById(feedCardPk);
    }

    public FeedCard findFeedCard(Long feedCardPk) {
        return feedCardRepository.findById(feedCardPk)
                .orElseThrow(()->new EntityNotFoundException(ExceptionMessage.CARD_NOT_FOUND.getMessage()));
    }

    public boolean isExistFeedCard(Long feedCardPk) {
        return feedCardRepository.existsById(feedCardPk);
    }

    public FeedCard findByPk(Long feedCardPk) {
        return feedCardRepository.findById(feedCardPk)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.CARD_NOT_FOUND.getMessage()));
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

    public List<MyFeedCardDto> findByMemberPk(Long memberPk, Pageable pageable) {
        return feedCardRepository.findByMemberPk(memberPk, pageable).stream()
                .map(feed -> MyFeedCardDto.builder()
                        .id(feed.getPk().toString())
                        .content(feed.getContent())
                        .backgroundImgUrl(imgService.findImgUrl(feed.getImgType(), feed.getImgName()))
                        .font(feed.getFont())
                        .fontSize(feed.getFontSize())
                        .build())
                .toList();
    }
}
