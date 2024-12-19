package com.sooum.api.notification.service;

import com.sooum.api.img.service.AWSImgService;
import com.sooum.api.notification.dto.NotificationDto;
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

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NotificationUseCase {
    private final AWSImgService awsImgService;
    private final MemberService memberService;
    private final NotificationHistoryService notificationHistoryService;

    public boolean setNotificationToRead(Long memberPk, Long notificationPk) {
        if(isNotificationOwner(memberPk, notificationPk)){
            notificationHistoryService.updateToRead(notificationPk);
            return true;
        }
        return false;
    }

    private boolean isNotificationOwner(Long memberPk, Long notificationPk) {
        return notificationHistoryService.findToMember(notificationPk).getPk().equals(memberPk);
    }

    public NotificationDto.NotificationCntResponse findUnreadNotificationCntResponse(Long memberPk) {
        return NotificationDto.NotificationCntResponse.builder()
                .unreadCnt(notificationHistoryService.findUnreadNotificationCount(memberPk))
                .build();
    }

    public NotificationDto.NotificationCntResponse findUnreadCardNotificationCntResponse(Long memberPk) {
        return NotificationDto.NotificationCntResponse.builder()
                .unreadCnt(notificationHistoryService.findUnreadCardNotificationCount(memberPk))
                .build();
    }

    public NotificationDto.NotificationCntResponse findUnreadLikeNotificationCntResponse(Long memberPk) {
        return NotificationDto.NotificationCntResponse.builder()
                .unreadCnt(notificationHistoryService.findUnreadLikeNotificationCount(memberPk))
                .build();

    }

    /**
     * {@code memberPk}가 읽지않은 전체 알림을 조회하여 DTO로 변환 후 반환합니다.
     * <p>전체 알림 조회는 시스템 알림이 포함될 수 있기 때문에, 시스템 알림인 경우 알림 화면에 카드 정보가 노출되지 않으니 DTO로 변환 시 imgUrl에 null을 전달합니다.
     *
     * @param memberPk 알림을 조회하는 사용자의 Pk.
     * @param lastPk 이전 페이지의 마지막 알림 Pk. 최초 조회는 null을 입력합니다.
     * @return {@literal List<NotificationDto.NotificationInfoResponse>}
     * @since 2024-12-14
     * @author jungwoo
     */
    public List<NotificationDto.CommonNotificationInfo> findUnreadNotificationResponses(Long memberPk, Optional<Long> lastPk) {

        return notificationHistoryService.findUnreadNotifications(memberPk, lastPk).stream()
                .map(notification -> {
                    if (isSystemNotification(notification)) {
                        if (isDeleteNotification(notification)) {
                            return NotificationDto.DeleteNotificationInfoResponse.of(notification);
                        }
                        if (isBlockedNotification(notification)) {
                            return NotificationDto.BlockedNotificationInfoResponse.of(notification);
                        }
                    }
                    return NotificationDto.NotificationInfoResponse
                            .of(notification, awsImgService.findCardImgUrl(notification.getImgType(), notification.getImgName()));
                })
                .toList();
    }

    private static boolean isSystemNotification(NotificationHistory notification) {
        return isDeleteNotification(notification) || isBlockedNotification(notification);
    }

    private static boolean isBlockedNotification(NotificationHistory notification) {
        return notification.getNotificationType().equals(NotificationType.BLOCKED);
    }

    private static boolean isDeleteNotification(NotificationHistory notification) {
        return notification.getNotificationType().equals(NotificationType.DELETED);
    }

    public List<NotificationDto.CommonNotificationInfo> findReadNotificationResponses(Long memberPk, Optional<Long> lastPk) {
        return notificationHistoryService.findReadNotifications(memberPk, lastPk).stream()
                .map(notification -> NotificationDto.NotificationInfoResponse
                        .of(notification, awsImgService.findCardImgUrl(notification.getImgType(), notification.getImgName()))
                )
                .toList();
    }

    public List<NotificationDto.CommonNotificationInfo> findUnreadCardNotificationResponses(Long memberPk, Optional<Long> lastPk) {
        return notificationHistoryService.findUnreadCardNotifications(memberPk, lastPk).stream()
                .map(notification -> NotificationDto.NotificationInfoResponse
                        .of(notification, awsImgService.findCardImgUrl(notification.getImgType(), notification.getImgName()))
                )
                .toList();
    }

    public List<NotificationDto.CommonNotificationInfo> findReadCardNotificationResponses(Long memberPk, Optional<Long> lastPk) {
        return notificationHistoryService.findReadCardNotifications(memberPk, lastPk).stream()
                .map(notification -> NotificationDto.NotificationInfoResponse
                        .of(notification, awsImgService.findCardImgUrl(notification.getImgType(), notification.getImgName()))
                )
                .toList();
    }

    public List<NotificationDto.CommonNotificationInfo> findUnreadLikeNotificationResponses(Long memberPk, Optional<Long> lastPk) {
        return notificationHistoryService.findUnreadLikeNotifications(memberPk, lastPk).stream()
                .map(notification -> NotificationDto.NotificationInfoResponse
                        .of(notification, awsImgService.findCardImgUrl(notification.getImgType(), notification.getImgName()))
                )
                .toList();
    }

    public List<NotificationDto.CommonNotificationInfo> findReadLikeNotificationResponses(Long memberPk, Optional<Long> lastPk) {
        return notificationHistoryService.findReadLikeNotifications(memberPk, lastPk).stream()
                .map(notification -> NotificationDto.NotificationInfoResponse
                        .of(notification, awsImgService.findCardImgUrl(notification.getImgType(), notification.getImgName()))
                )
                .toList();
    }

    @Transactional
    public void saveCommentWriteHistory(Long fromMemberPk, Card card) {
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
    public void saveBlockedHistoryAndDeletePreviousHistories(Long toMemberPk) {
        notificationHistoryService.deletePreviousBlockedHistories(toMemberPk);

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
