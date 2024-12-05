package com.sooum.batch.card.batch.service;

import com.sooum.batch.card.batch.repository.CommentCardBatchRepository;
import com.sooum.batch.card.batch.repository.FeedLikeBatchRepository;
import com.sooum.batch.card.batch.repository.PopularCardBatchRepository;
import com.sooum.data.card.entity.FeedCard;
import com.sooum.data.card.entity.PopularFeed;
import com.sooum.data.card.entity.popularitytype.PopularityType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SavePopularCardService {
    private final CommentCardBatchRepository commentCardBatchRepository;
    private final FeedLikeBatchRepository feedLikeBatchRepository;
    private final PopularCardBatchRepository popularCardBatchRepository;

    private final static int PAGE_SIZE = 100;

    public void savePopularCardByLike() {
        List<FeedCard> popularCondFeedCards = feedLikeBatchRepository.findPopularCondFeedCards(PageRequest.ofSize(PAGE_SIZE));

        savePopularCard(popularCondFeedCards, PopularityType.LIKE);
    }

    public void savePopularCardByComment() {
        List<FeedCard> popularCondFeedCards = commentCardBatchRepository.findPopularCondFeedCards(PageRequest.ofSize(PAGE_SIZE));

        savePopularCard(popularCondFeedCards, PopularityType.COMMENT);
    }

    public void savePopularCard(List<FeedCard> popularCondFeedCards, PopularityType popularityType) {
        List<PopularFeed> popularFeeds = popularCondFeedCards.stream()
                .map(feedCard -> PopularFeed.builder()
                        .popularCard(feedCard)
                        .popularityType(popularityType)
                        .build())
                .toList();

//        popularCardBatchRepository.saveAll(popularFeeds);
        for (PopularFeed popularFeed : popularFeeds) {
            try {
                popularCardBatchRepository.saveAndFlush(popularFeed);
            } catch (Exception e) {
            }
        }
    }
}
