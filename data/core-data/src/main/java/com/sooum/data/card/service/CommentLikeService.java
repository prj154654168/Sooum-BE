package com.sooum.data.card.service;

import com.sooum.data.card.entity.CommentCard;
import com.sooum.data.card.entity.CommentLike;
import com.sooum.data.card.repository.CommentLikeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentLikeService {
    private final CommentLikeRepository commentLikeRepository;

    public Optional<CommentLike> findCommentLikedOp(Long targetFeedCardPk, Long requesterPk) {
        return commentLikeRepository.findCommentLiked(targetFeedCardPk, requesterPk);
    }

    public void save(CommentLike commentLike) {
        commentLikeRepository.save(commentLike);
    }

    public CommentLike findCommentLiked(Long likedFeedCardPk, Long likedMemberPk) {
        return commentLikeRepository.findCommentLiked(likedFeedCardPk, likedMemberPk)
                .orElseThrow(() -> new EntityNotFoundException("좋아요 이력을 찾을 수 않습니다."));
    }

    public List<CommentLike> findByTargetCards(List<CommentCard> commentCards) {
        return commentLikeRepository.findByTargetCardIn(commentCards);
    }

    @Transactional
    public void deleteAllFeedLikes(Long commentCardPk) {
        commentLikeRepository.deleteAllInBatch(commentLikeRepository.findAllByTargetCard_Pk(commentCardPk));
    }

    @Transactional
    public void deleteByCommentCards(List<CommentCard> comments) {
        commentLikeRepository.deleteByCommentCard(comments);
    }
      
    public int countLike(Long cardPk) {
        return commentLikeRepository.countByTargetCard_Pk(cardPk);
    }

    public boolean isLiked(Long cardPk, Long memberPk) {
        return commentLikeRepository.findExistCommentLike(cardPk, memberPk).isPresent();
    }

    @Transactional
    public void deleteAllMemberLikes(Long memberPk){
        commentLikeRepository.deleteAllMemberLikes(memberPk);
    }
}