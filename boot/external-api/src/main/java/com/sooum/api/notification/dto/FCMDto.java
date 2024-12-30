package com.sooum.api.notification.dto;

import com.sooum.data.member.entity.devicetype.DeviceType;
import com.sooum.data.notification.entity.notificationtype.NotificationType;
import lombok.Builder;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

public class FCMDto {
    @Getter
    private abstract static class CommonFCM extends ApplicationEvent {
        private Long notificationId;
        private DeviceType targetDeviceType;
        private String targetFcmToken;
        private NotificationType notificationType;

        public CommonFCM(Object source, DeviceType targetDeviceType, String targetFcmToken, Long notificationId, NotificationType notificationType) {
            super(source);
            this.notificationId = notificationId;
            this.targetDeviceType = targetDeviceType;
            this.targetFcmToken = targetFcmToken;
            this.notificationType = notificationType;
        }
    }

    @Getter
    public static class SystemFcm extends CommonFCM {
        public SystemFcm(Object source, DeviceType deviceType, String fcmToken, Long notificationId, NotificationType notificationType) {
            super(source, deviceType, fcmToken, notificationId, notificationType);
        }
    }

    @Getter
    public static class GeneralFcm extends CommonFCM {
        private String requesterNickname;
        private Long targetCardPk;

        public GeneralFcm(Object source, DeviceType targetDeviceType, String targetFcmToken, Long targetCardPk, String requesterNickname, Long notificationId, NotificationType notificationType) {
            super(source, targetDeviceType, targetFcmToken, notificationId, notificationType);
            this.targetCardPk = targetCardPk;
            this.requesterNickname = requesterNickname;
        }
    }

    @Getter
    public static class SystemFcmSendEvent extends SystemFcm {
        @Builder
        public SystemFcmSendEvent(Object source, DeviceType targetDeviceType, String targetFcmToken, NotificationType notificationType, Long notificationId) {
            super(source, targetDeviceType, targetFcmToken, notificationId, notificationType);

            if (notificationType.isNotSystem()) {
                throw new IllegalArgumentException();
            }
        }
    }

    @Getter
    public static class GeneralFcmSendEvent extends GeneralFcm {
        @Builder
        public GeneralFcmSendEvent(Object source, DeviceType targetDeviceType, String targetFcmToken, Long targetCardPk, String requesterNickname, NotificationType notificationType, Long notificationId) {
            super(source, targetDeviceType, targetFcmToken, targetCardPk, requesterNickname, notificationId, notificationType);

            if (notificationType.isNotGeneral()) {
                throw new IllegalArgumentException();
            }
        }
    }
}
