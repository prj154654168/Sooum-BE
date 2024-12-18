package com.sooum.api.notification.dto;

import com.sooum.data.member.entity.devicetype.DeviceType;
import com.sooum.data.notification.entity.notificationtype.NotificationType;
import lombok.Builder;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

public class FCMDto {
    @Getter
    private abstract static class CommonFCM extends ApplicationEvent {
        private DeviceType targetDeviceType;
        private String targetFcmToken;

        public CommonFCM(Object source, DeviceType targetDeviceType, String targetFcmToken) {
            super(source);
            this.targetDeviceType = targetDeviceType;
            this.targetFcmToken = targetFcmToken;
        }
    }

    @Getter
    public static class SystemFcm extends CommonFCM {
        public SystemFcm(Object source, DeviceType deviceType, String fcmToken) {
            super(source, deviceType, fcmToken);
        }
    }

    @Getter
    public static class GeneralFcm extends CommonFCM {
        private String requesterNickname;
        private Long targetCardPk;

        public GeneralFcm(Object source, DeviceType targetDeviceType, String targetFcmToken, Long targetCardPk, String requesterNickname) {
            super(source, targetDeviceType, targetFcmToken);
            this.targetCardPk = targetCardPk;
            this.requesterNickname = requesterNickname;
        }
    }

    @Getter
    public static class SystemFcmSendEvent extends SystemFcm {
        private NotificationType notificationType;

        @Builder
        public SystemFcmSendEvent(Object source, DeviceType targetDeviceType, String targetFcmToken, NotificationType notificationType) {
            super(source, targetDeviceType, targetFcmToken);

            if (notificationType.isNotSystem()) {
                throw new IllegalArgumentException();
            }
            this.notificationType = notificationType;
        }
    }

    @Getter
    public static class GeneralFcmSendEvent extends GeneralFcm {
        private NotificationType notificationType;

        @Builder
        public GeneralFcmSendEvent(Object source, DeviceType targetDeviceType, String targetFcmToken, Long targetCardPk, String requesterNickname, NotificationType notificationType) {
            super(source, targetDeviceType, targetFcmToken, targetCardPk, requesterNickname);

            if (notificationType.isNotGeneral()) {
                throw new IllegalArgumentException();
            }
            this.notificationType = notificationType;
        }
    }
}
