package com.sooum.core.domain.card.service;

import com.sooum.core.domain.card.entity.FeedCard;
import com.sooum.core.domain.card.entity.FeedLike;
import com.sooum.core.domain.card.repository.FeedLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FeedLikeService {
    private final FeedLikeRepository feedLikeRepository;

    public List<FeedLike> findByTargetCards(List<FeedCard> targetCards) {
        return feedLikeRepository.findByTargetList(targetCards);
    }
}
