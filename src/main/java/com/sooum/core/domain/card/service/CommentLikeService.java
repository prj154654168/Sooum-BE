package com.sooum.core.domain.card.service;

import com.sooum.core.domain.card.entity.CommentCard;
import com.sooum.core.domain.card.entity.CommentLike;
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

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentLikeService {
    private final MemberService memberService;
    private final CommentCardService commentCardService;
    private final CommentLikeRepository commentLikeRepository;

    @Transactional
    public void createCommentLike(Long targetCommentCardPk, Long requesterPk) {
        if (commentLikeRepository.existsByTargetCardPkAndLikedMemberPk(targetCommentCardPk, requesterPk)) {
            throw new EntityExistsException(ExceptionMessage.ALREADY_CARD_LIKED.getMessage());
        }

        Member likedMember = memberService.findByPk(requesterPk);
        CommentCard targetCard = commentCardService.findByPk(targetCommentCardPk);
        commentLikeRepository.save(
                CommentLike.builder()
                        .likedMember(likedMember)
                        .targetCard(targetCard)
                        .build()
        );
    }

    @Transactional
    public void deleteCommentLike(Long likedFeedCardPk, Long likedMemberPk) {
        CommentLike feedLiked = commentLikeRepository.findCommentLiked(likedFeedCardPk, likedMemberPk)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.LIKE_NOT_FOUND.getMessage()));
        commentLikeRepository.delete(feedLiked);
    }

    public List<CommentLike> findByTargetCards(List<CommentCard> commentCards) {
        return commentLikeRepository.findByTargetCardIn(commentCards);
    }
}
