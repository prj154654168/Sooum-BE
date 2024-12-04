package com.sooum.batch.card.batch.service;

import com.sooum.batch.card.batch.repository.CommentCardBatchRepository;
import com.sooum.batch.card.batch.repository.FeedLikeBatchRepository;
import com.sooum.batch.card.batch.repository.PopularCardBatchRepository;
import com.sooum.data.card.entity.FeedCard;
import com.sooum.data.card.entity.PopularFeed;
import com.sooum.data.card.entity.popularitytype.PopularityType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    private void savePopularCard(List<FeedCard> popularCondFeedCards, PopularityType popularityType) {
        List<PopularFeed> popularFeeds = popularCondFeedCards.stream()
                .map(feedCard -> PopularFeed.builder()
                        .popularCard(feedCard)
                        .popularityType(popularityType)
                        .build())
                .toList();

        popularCardBatchRepository.saveAll(popularFeeds);
        //        for (PopularFeed popularFeed : popularFeeds) {
//            try {
//                popularCardBatchRepository.save(popularFeed);
//            } catch (Exception ignored) {
//            }
//        }
    }
}
