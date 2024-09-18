package com.sooum.core.domain.card.service;

import com.sooum.core.domain.card.entity.CommentCard;
import com.sooum.core.domain.card.entity.FeedCard;
import com.sooum.core.domain.card.repository.CommentCardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentCardService {
    private final CommentCardRepository commentCardRepository;

    public List<CommentCard> findByMasterCards(List<FeedCard> masterCards) {
        return commentCardRepository.findByMasterCardIn(masterCards);
    }
}
