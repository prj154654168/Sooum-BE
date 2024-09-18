package com.sooum.core.domain.card.service;

import com.sooum.core.domain.card.entity.CommentCard;
import com.sooum.core.domain.card.entity.FeedCard;
import com.sooum.core.domain.card.repository.CommentCardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentCardService {
    private final CommentCardRepository commentCardRepository;


    public List<CommentCard> findByTargetList(List<FeedCard> targetList) {
        return commentCardRepository.findByTargetList(targetList);
    }
}
