package com.sooum.api.card.service;

import com.sooum.api.notification.service.NotificationUseCase;
import com.sooum.api.notification.service.SendFCMService;
import com.sooum.data.card.entity.CommentCard;
import com.sooum.data.card.entity.CommentLike;
import com.sooum.data.card.entity.FeedCard;
import com.sooum.data.card.entity.FeedLike;
import com.sooum.data.card.service.CommentCardService;
import com.sooum.data.card.service.CommentLikeService;
import com.sooum.data.card.service.FeedCardService;
import com.sooum.data.card.service.FeedLikeService;
import com.sooum.data.member.entity.Member;
import com.sooum.data.member.entity.devicetype.DeviceType;
import com.sooum.data.member.service.MemberService;
import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CardLikeService {
    private final FeedCardService feedCardService;
    private final CommentCardService commentCardService;
    private final FeedLikeService feedLikeService;
    private final CommentLikeService commentLikeService;
    private final MemberService memberService;
    private final NotificationUseCase notificationUseCase;
    private final SendFCMService sendFCMService;

    @Transactional
    public void createFeedLike(Long targetFeedCardPk, Long requesterPk) {
        Optional<FeedLike> findFeedLiked = feedLikeService.findFeedLikedOp(targetFeedCardPk, requesterPk);
        if (findFeedLiked.isPresent()) {
            if (!findFeedLiked.get().isDeleted()) {
                throw new EntityExistsException("이미 좋아요 이력이 존재합니다.");
            }

            findFeedLiked.get().create();
            return;
        }

        Member likedMember = memberService.findMember(requesterPk);
        FeedCard targetCard = feedCardService.findByPk(targetFeedCardPk);
        feedLikeService.save(
                FeedLike.builder()
                        .likedMember(likedMember)
                        .targetCard(targetCard)
                        .build()
        );

        notificationUseCase.saveFeedLikeHistory(requesterPk, targetCard);
    }

    @Transactional
    public void deleteFeedLike(Long likedFeedCardPk, Long likedMemberPk) {
        FeedLike feedLiked = feedLikeService.findFeedLiked(likedFeedCardPk, likedMemberPk);

        if (feedLiked.isDeleted()) {
            throw new EntityExistsException("이미 삭제된 좋아요입니다.");
        }

        feedLiked.delete();
    }

    @Transactional
    public void createCommentLike(Long targetFeedCardPk, Long requesterPk) {
        Optional<CommentLike> findCommentLiked = commentLikeService.findCommentLikedOp(targetFeedCardPk, requesterPk);
        if (findCommentLiked.isPresent()) {
            if (!findCommentLiked.get().isDeleted()) {
                throw new EntityExistsException("이미 좋아요 이력이 존재합니다.");
            }

            findCommentLiked.get().create();
            return;
        }

        Member likedMember = memberService.findMember(requesterPk);
        CommentCard targetCard = commentCardService.findByPk(targetFeedCardPk);
        commentLikeService.save(CommentLike.builder()
                .likedMember(likedMember)
                .targetCard(targetCard)
                .build());

        notificationUseCase.saveCommentLikeHistory(requesterPk, targetCard);
        sendFCMService.sendCommentLikeMsg(DeviceType.ANDROID, targetFeedCardPk, targetCard.getWriter().getNickname(), "c78DeTomQF6yjOy86mcJ11:APA91bGtHV5-17g6u8d7vVH_LrYtqoJiVgmlMpHG3ahV9wMteIw2I_77jJviBWxPXWjXEC8W3BAcNQNqZaEZn6eErsbKTIMuMMP8aH88UCkK1QEl3mtAtcc");
    }

    @Transactional
    public void deleteCommentLike(Long likedFeedCardPk, Long likedMemberPk) {
        CommentLike findCommentLiked = commentLikeService.findCommentLiked(likedFeedCardPk, likedMemberPk);

        if (findCommentLiked.isDeleted()) {
            throw new EntityExistsException("이미 삭제된 좋아요입니다.");
        }

        findCommentLiked.delete();
    }
}
