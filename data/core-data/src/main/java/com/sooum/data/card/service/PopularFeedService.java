package com.sooum.data.card.service;

import com.sooum.data.card.entity.FeedCard;
import com.sooum.data.card.repository.PopularFeedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PopularFeedService {
    private final PopularFeedRepository popularFeedRepository;

    public void deletePopularCard(Long cardId) {
        popularFeedRepository.deletePopularCard(cardId);
    }

    public List<FeedCard> getPopularFeeds(PageRequest pageRequest) {
        return popularFeedRepository.findPopularFeeds(pageRequest);
    }
}
