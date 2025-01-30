package com.sooum.api.card.service;

import com.sooum.data.card.entity.CommentCard;
import com.sooum.data.card.entity.FeedCard;
import com.sooum.data.card.entity.parenttype.CardType;
import com.sooum.data.card.service.*;
import com.sooum.data.img.service.CardImgService;
import com.sooum.data.notification.service.NotificationHistoryService;
import com.sooum.data.report.service.CommentReportService;
import com.sooum.data.report.service.FeedReportService;
import com.sooum.data.tag.service.CommentTagService;
import com.sooum.data.tag.service.FeedTagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteCardUseCase {
    private final FeedCardService feedCardService;
    private final FeedLikeService feedLikeService;
    private final FeedReportService feedReportService;
    private final FeedTagService feedTagService;

    private final CommentCardService commentCardService;
    private final CommentLikeService commentLikeService;
    private final CommentReportService commentReportService;
    private final CommentTagService commentTagService;

    private final PopularFeedService popularFeedService;
    private final NotificationHistoryService notificationHistoryService;
    private final CardImgService cardImgService;

    @Transactional
    public void deleteCard(Long cardPk, Long writerPk) {
        CardType cardType = commentCardService.findCardType(cardPk);
        switch (cardType) {
            case FEED_CARD -> deleteFeedCard(cardPk, writerPk);
            case COMMENT_CARD -> deleteCommentCard(cardPk, writerPk);
        }
    }

    private void deleteFeedCard(Long feedCardPk, Long writerPk) {
        validateFeedCardOwner(feedCardPk, writerPk);
        FeedCard feedCard = feedCardService.findFeedCard(feedCardPk);
        deleteFeedCardDependencies(feedCard);
        feedCardService.deleteFeedCard(feedCardPk);
    }

    private void deleteCommentCard(Long commentCardPk, Long writerPk) {
        validateCommentCardOwner(commentCardPk, writerPk);
        CommentCard commentCard = commentCardService.findCommentCard(commentCardPk);
        deleteCommentCardDependencies(commentCard);
        commentCardService.deleteCommentCard(commentCardPk);
    }

    private void validateFeedCardOwner(Long commentCardPk, Long writerPk) {
        if (isNotFeedCardOwner(commentCardPk, writerPk)) {
            throw new IllegalArgumentException("카드 작성자만 카드 삭제가 가능합니다.");
        }
    }

    private void validateCommentCardOwner(Long commentCardPk, Long writerPk) {
        if (isNotCommentCardOwner(commentCardPk, writerPk)) {
            throw new IllegalArgumentException("카드 작성자만 카드 삭제가 가능합니다.");
        }
    }

    private boolean isNotCommentCardOwner(Long commentCardPk, Long writerPk) {
        return !commentCardService.findCommentCard(commentCardPk).getWriter().getPk().equals(writerPk);
    }

    private boolean isNotFeedCardOwner(Long feedCardPk, Long writerPk) {
        return !feedCardService.findFeedCard(feedCardPk).getWriter().getPk().equals(writerPk);
    }

    private void deleteFeedCardDependencies(FeedCard feedCard) {
        feedTagService.deleteByFeedCardPk(feedCard.getPk());
        cardImgService.deleteUserUploadPic(feedCard.getImgName());
        feedLikeService.deleteAllFeedLikes(feedCard.getPk());
        feedReportService.deleteReport(feedCard.getPk());
        popularFeedService.deletePopularCard(feedCard.getPk());
        notificationHistoryService.deleteNotification(feedCard.getPk());
    }

    private void deleteCommentCardDependencies(CommentCard commentCard) {
        commentTagService.deleteByCommentCardPk(commentCard.getPk());
        cardImgService.deleteUserUploadPic(commentCard.getImgName());
        commentLikeService.deleteAllFeedLikes(commentCard.getPk());
        commentReportService.deleteReport(commentCard);
        notificationHistoryService.deleteNotification(commentCard.getPk());
    }
}
