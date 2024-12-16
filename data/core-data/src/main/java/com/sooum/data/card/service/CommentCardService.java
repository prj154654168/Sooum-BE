package com.sooum.data.card.service;

import com.sooum.data.card.entity.CommentCard;
import com.sooum.data.card.entity.FeedCard;
import com.sooum.data.card.entity.parenttype.CardType;
import com.sooum.data.card.repository.CommentCardRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentCardService {
    private final CommentCardRepository commentCardRepository;
    private final static int MAX_PAGE_SIZE = 50;

    public boolean hasChildCard(Long parentCardPk) {
        return !findChildCommentCardList(parentCardPk).isEmpty();
    }

    public List<CommentCard> findChildCommentCardList(Long parentCardPk) {
        return commentCardRepository.findChildCards(parentCardPk);
    }

    public void deleteCommentCard(Long commentCardPk) {
        commentCardRepository.deleteById(commentCardPk);
    }

    public List<CommentCard> findByMasterCardPk(Long masterCardPk) {
        return commentCardRepository.findAllByMasterCard(masterCardPk);
    }

    public List<CommentCard> findCommentCardsIn(List<FeedCard> targetList) {
        List<Long> feedCardPks = targetList.stream().map(FeedCard::getPk).toList();
        return commentCardRepository.findCommentCardsIn(feedCardPks);
    }

    public CommentCard findCommentCard(Long commentCardPk) {
        return commentCardRepository.findCommentCard(commentCardPk)
                .orElseThrow(() -> new EntityNotFoundException("댓글 카드를 찾을 수 없습니다."));
    }

    public CommentCard findByPk(Long commentCardPk) {
        return commentCardRepository.findById(commentCardPk)
                .orElseThrow(() -> new EntityNotFoundException("댓글 카드를 찾을 수 없습니다."));
    }

    public Optional<CommentCard> findOptCommentCard(Long commentCardPk) {
        return commentCardRepository.findById(commentCardPk);
    }

    public boolean isExistCommentCard(Long commentCardPk) {
        return commentCardRepository.existsById(commentCardPk);
    }

    public CardType findCardType(Long cardPk) {
        return isExistCommentCard(cardPk) ? CardType.COMMENT_CARD : CardType.FEED_CARD;
    }

    public List<CommentCard> findCommentsByLastPk(Long currentCardPk, Optional<Long> lastPk, List<Long> blockMemberPks) {
        PageRequest pageRequest = PageRequest.ofSize(MAX_PAGE_SIZE);
        return commentCardRepository.findCommentsInfo(currentCardPk, lastPk.orElse(null), blockMemberPks, pageRequest);
    }

    public List<CommentCard> findChildCommentsByParents(List<CommentCard> commentCards) {
        List<Long> commentCardsPk = commentCards.stream().map(CommentCard::getPk).toList();
        return commentCardRepository.findChildCards(commentCardsPk);
    }

    public int countComment(Long parentCardPk) {
        return commentCardRepository.countCommentsByParentCard(parentCardPk);
    }

    public void saveComment(CommentCard commentCard) {
        commentCardRepository.save(commentCard);
    }

    public List<CommentCard> findCommentList(Long memberPk, Optional<Long> lastPk) {
        PageRequest pageRequest = PageRequest.ofSize(30);
        return commentCardRepository.findCommentCards(memberPk, lastPk.orElse(null), pageRequest);
    }

    public void deleteCommentCardByMemberPk(Long memberPk) {
        commentCardRepository.deleteCommentCardByMemberPk(memberPk);
    }

    public void deleteAllComments(List<CommentCard> comments) {
        commentCardRepository.deleteAllInBatch(comments);
    }
}
