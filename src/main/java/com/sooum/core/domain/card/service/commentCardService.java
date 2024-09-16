package com.sooum.core.domain.card.service;

import com.sooum.core.domain.card.entity.CommentCard;
import com.sooum.core.domain.card.entity.FeedCard;
import com.sooum.core.domain.card.repository.CommentCardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class commentCardService {
    private final CommentCardRepository commentCardRepository;


    public List<CommentCard> findByTargetList(List<FeedCard> targetList) {
        return commentCardRepository.findByTargetList(targetList);
    }
}
