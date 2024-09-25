package com.sooum.core.domain.card.service;

import com.sooum.core.domain.card.entity.CommentCard;
import com.sooum.core.domain.card.entity.FeedCard;
import com.sooum.core.domain.card.repository.CommentCardRepository;
import com.sooum.core.global.exceptionmessage.ExceptionMessage;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentCardService {
    private final CommentCardRepository commentCardRepository;

    public void deleteOnlyDeletedChild(Long commentCardPk) {
        List<CommentCard> childCards = commentCardRepository.findChildCards(commentCardPk);
        if (childCards.size() == 1 && childCards.get(0).isDeleted()) {
            commentCardRepository.delete(childCards.get(0));
        }
    }

    public boolean hasChildCard(Long parentCardPk) {
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
        return commentCardRepository.findCommentCard(commentCardPk)
                .orElseThrow(()->new EntityNotFoundException(ExceptionMessage.CARD_NOT_FOUND.getMessage()));
    }

    public CommentCard findByPk(Long commentCardPk) {
        return commentCardRepository.findById(commentCardPk)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.CARD_NOT_FOUND.getMessage()));
    }

    public boolean isExistCommentCard(Long commentCardPk) {
        return commentCardRepository.existsById(commentCardPk);
    }
}
