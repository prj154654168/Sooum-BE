package com.sooum.core.domain.card.service;

import com.sooum.core.domain.card.entity.FeedCard;
import com.sooum.core.domain.card.entity.FeedLike;
import com.sooum.core.domain.card.repository.FeedLikeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedLikeService {
    private final FeedLikeRepository feedLikeRepository;

    public List<FeedLike> findByTargetList(List<FeedCard> targetList){
        return feedLikeRepository.findByTargetList(targetList);
    }

}