package com.sooum.api.notification.service;

import com.sooum.data.card.entity.Card;
import com.sooum.data.card.entity.CommentCard;
import com.sooum.data.card.entity.FeedCard;
import com.sooum.data.member.service.MemberService;
import com.sooum.data.notification.entity.NotificationHistory;
import com.sooum.data.notification.entity.notificationtype.NotificationType;
import com.sooum.data.notification.service.NotificationHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SaveNotificationService {
    private final NotificationHistoryService notificationHistoryService;
    private final MemberService memberService;

    @Transactional
    public void saveCommentWriteHistory(Long fromMemberPk, Card card) {
        if (fromMemberPk.equals(card.getWriter().getPk())) {
            return;
        }

        notificationHistoryService.save(
                NotificationHistory.builder()
                        .notificationType(NotificationType.COMMENT_WRITE)
                        .fromMember(memberService.findMember(fromMemberPk))
                        .toMember(card.getWriter())
                        .card(card)
                        .build()
        );
    }

    @Transactional
    public void saveFeedLikeHistory(Long fromMemberPk, FeedCard feedCard) {
        if (fromMemberPk.equals(feedCard.getWriter().getPk())) {
            return;
        }

        notificationHistoryService.save(
                NotificationHistory.builder()
                        .notificationType(NotificationType.FEED_LIKE)
                        .fromMember(memberService.findMember(fromMemberPk))
                        .toMember(feedCard.getWriter())
                        .card(feedCard)
                        .build()
        );
    }

    @Transactional
    public void saveCommentLikeHistory(Long fromMemberPk, CommentCard commentCard) {
        if (fromMemberPk.equals(commentCard.getWriter().getPk())) {
            return;
        }

        notificationHistoryService.save(
                NotificationHistory.builder()
                        .notificationType(NotificationType.COMMENT_LIKE)
                        .fromMember(memberService.findMember(fromMemberPk))
                        .toMember(commentCard.getWriter())
                        .card(commentCard)
                        .build()
        );
    }

    @Transactional
    public void saveBlockedHistory(Long toMemberPk) {
        notificationHistoryService.save(
                NotificationHistory.builder()
                        .notificationType(NotificationType.BLOCKED)
                        .toMember(memberService.findMember(toMemberPk))
                        .build()
        );
    }

    @Transactional
    public void saveCardDeletedHistoryByReport(Long toMemberPk) {
        notificationHistoryService.save(
                NotificationHistory.builder()
                        .notificationType(NotificationType.DELETED)
                        .toMember(memberService.findMember(toMemberPk))
                        .build()
        );
    }
}
