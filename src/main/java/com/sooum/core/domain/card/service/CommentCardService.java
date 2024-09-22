package com.sooum.core.domain.card.service;

import com.sooum.core.domain.card.entity.CommentCard;
import com.sooum.core.domain.card.entity.FeedCard;
import com.sooum.core.domain.card.repository.CommentCardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentCardService {
    private final CommentCardRepository commentCardRepository;

    public boolean hasCommentCard(Long parentCardPk) {
        return !findChildCommentCardList(parentCardPk).isEmpty();
    }

    public List<CommentCard> findChildCommentCardList(Long parentCardPk) {
        return commentCardRepository.findChildCards(parentCardPk);
    }

    public void deleteCommentCard(Long commentCardPk) {
        commentCardRepository.deleteById(commentCardPk);
    }

    public List<CommentCard> findByMasterCards(List<FeedCard> masterCards) {
        return commentCardRepository.findByMasterCardIn(masterCards);
    }

    public List<CommentCard> findByTargetList(List<FeedCard> targetList) {
        return commentCardRepository.findByTargetList(targetList);
    }

    public CommentCard findCommentCard(Long commentCardPk) {
        return commentCardRepository.findCommentCard(commentCardPk).orElseThrow(NoSuchElementException::new);
    }
}
