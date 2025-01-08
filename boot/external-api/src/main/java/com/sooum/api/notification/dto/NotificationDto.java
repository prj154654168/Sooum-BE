package com.sooum.api.notification.dto;

import com.sooum.data.card.entity.font.Font;
import com.sooum.data.card.entity.fontsize.FontSize;
import com.sooum.data.notification.entity.NotificationHistory;
import com.sooum.data.notification.entity.notificationtype.NotificationType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.Link;

import java.time.LocalDateTime;

public class NotificationDto {
    @Getter
    @NoArgsConstructor
    public static class NotificationCntResponse {
        String unreadCnt;

        @Builder
        public NotificationCntResponse(String unreadCnt) {
            this.unreadCnt = unreadCnt;
        }
    }

    @Getter
    public abstract static class CommonNotificationInfo {
        Long notificationId;
        NotificationType notificationType;
        LocalDateTime createTime;

    public CommonNotificationInfo(Long notificationId, NotificationType notificationType, LocalDateTime createTime) {
        this.notificationId = notificationId;
        this.notificationType = notificationType;
        this.createTime = createTime;
    }
}

    @Getter
    public static class NotificationInfoResponse extends CommonNotificationInfo {

        Long targetCardId;
        Link imgUrl;
        String content;
        Font font;
        FontSize fontSize;
        String nickName;

        @Builder
        public NotificationInfoResponse(Long notificationId, NotificationType notificationType, LocalDateTime createTime, Long targetCardId, Link imgUrl, String content, Font font, FontSize fontSize, String nickName) {
            super(notificationId, notificationType, createTime);
            this.targetCardId = targetCardId;
            this.imgUrl = imgUrl;
            this.content = content;
            this.font = font;
            this.fontSize = fontSize;
            this.nickName = nickName;
        }

        public static CommonNotificationInfo of(NotificationHistory history, Link imgUrl) {
            return NotificationInfoResponse.builder()
                    .notificationId(history.getPk())
                    .targetCardId(history.getTargetCardPk())
                    .notificationType(history.getNotificationType())
                    .createTime(history.getCreatedAt())
                    .imgUrl(imgUrl)
                    .content(history.getContent())
                    .font(history.getFont())
                    .fontSize(history.getFontSize())
                    .nickName(history.getFromMember().getNickname())
                    .build();
        }
    }

    @Getter
    public static class DeleteNotificationInfoResponse extends CommonNotificationInfo {

        @Builder
        public DeleteNotificationInfoResponse(Long notificationId, NotificationType notificationType, LocalDateTime createTime) {
            super(notificationId, notificationType, createTime);
        }

        public static DeleteNotificationInfoResponse of(NotificationHistory history) {
            return DeleteNotificationInfoResponse.builder()
                    .notificationId(history.getPk())
                    .notificationType(history.getNotificationType())
                    .createTime(history.getCreatedAt())
                    .build();
        }
    }

    @Getter
    public static class BlockedNotificationInfoResponse extends CommonNotificationInfo {
        LocalDateTime blockExpirationDateTime;

        @Builder
        public BlockedNotificationInfoResponse(Long notificationId, NotificationType notificationType, LocalDateTime createTime, LocalDateTime blockExpirationDateTime) {
            super(notificationId, notificationType, createTime);
            this.blockExpirationDateTime = blockExpirationDateTime;
        }

        public static BlockedNotificationInfoResponse of(NotificationHistory history) {
            return BlockedNotificationInfoResponse.builder()
                    .notificationId(history.getPk())
                    .notificationType(history.getNotificationType())
                    .createTime(history.getCreatedAt())
                    .blockExpirationDateTime(history.getToMember().getUntilBan())
                    .build();
        }
    }
}
