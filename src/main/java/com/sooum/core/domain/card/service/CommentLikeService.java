package com.sooum.core.domain.card.service;

import com.sooum.core.domain.card.entity.CommentCard;
import com.sooum.core.domain.card.entity.CommentLike;
import com.sooum.core.domain.card.entity.FeedCard;
import com.sooum.core.domain.card.entity.FeedLike;
import com.sooum.core.domain.card.repository.CommentLikeRepository;
import com.sooum.core.domain.member.entity.Member;
import com.sooum.core.domain.member.service.MemberService;
import com.sooum.core.global.exceptionmessage.ExceptionMessage;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentLikeService {
    private final MemberService memberService;
    private final CommentCardService commentCardService;
    private final CommentLikeRepository commentLikeRepository;

    @Transactional
    public void createCommentLike(Long targetFeedCardPk, Long requesterPk) {
        Optional<CommentLike> findCommentLiked = commentLikeRepository.findCommentLiked(targetFeedCardPk, requesterPk);
        if (findCommentLiked.isPresent()) {
            if (!findCommentLiked.get().isDeleted()) {
                throw new EntityExistsException(ExceptionMessage.ALREADY_CARD_LIKED.getMessage());
            }

            findCommentLiked.get().create();
            return;
        }

        Member likedMember = memberService.findByPk(requesterPk);
        CommentCard targetCard = commentCardService.findByPk(targetFeedCardPk);
        commentLikeRepository.save(
                CommentLike.builder()
                        .likedMember(likedMember)
                        .targetCard(targetCard)
                        .build()
        );
    }

    @Transactional
    public void deleteCommentLike(Long likedFeedCardPk, Long likedMemberPk) {
        CommentLike findCommentLiked = commentLikeRepository.findCommentLiked(likedFeedCardPk, likedMemberPk)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.LIKE_NOT_FOUND.getMessage()));

        if (findCommentLiked.isDeleted()) {
            throw new EntityExistsException(ExceptionMessage.ALREADY_DELETE_CARD_LIKE.getMessage());
        }

        findCommentLiked.delete();
    }

    public List<CommentLike> findByTargetCards(List<CommentCard> commentCards) {
        return commentLikeRepository.findByTargetCardIn(commentCards);
    }

    public void deleteAllFeedLikes(Long commentCardPk) {
        commentLikeRepository.deleteAllInBatch(commentLikeRepository.findAllByTargetCard_Pk(commentCardPk));
    }

    public void deleteByCommentCards(List<CommentCard> comments) {
        commentLikeRepository.deleteByCommentCard(comments);
    }
      
    public int countLike(Long cardPk) {
        return commentLikeRepository.countByTargetCard_Pk(cardPk);
    }

    public boolean isLiked(Long cardPk, Long memberPk) {
        return commentLikeRepository.existsByTargetCardPkAndLikedMemberPk(cardPk, memberPk);
    }
}
