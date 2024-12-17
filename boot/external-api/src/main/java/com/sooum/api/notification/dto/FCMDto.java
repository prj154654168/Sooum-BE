package com.sooum.api.notification.dto;

import com.sooum.data.member.entity.devicetype.DeviceType;
import com.sooum.data.notification.entity.notificationtype.NotificationType;
import lombok.Builder;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

public class FCMDto {
    @Getter
    private abstract static class CommonFCM extends ApplicationEvent {
        private DeviceType deviceType;
        private String fcmToken;

        public CommonFCM(Object source, DeviceType deviceType, String fcmToken) {
            super(source);
            this.deviceType = deviceType;
            this.fcmToken = fcmToken;
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

        public GeneralFcm(Object source, DeviceType deviceType, String fcmToken, Long targetCardPk, String requesterNickname) {
            super(source, deviceType, fcmToken);
            this.targetCardPk = targetCardPk;
            this.requesterNickname = requesterNickname;
        }
    }

    @Getter
    public static class SystemFcmSendEvent extends SystemFcm {
        private NotificationType notificationType;

        @Builder
        public SystemFcmSendEvent(Object source, DeviceType deviceType, String fcmToken, NotificationType notificationType) {
            super(source, deviceType, fcmToken);

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
        public GeneralFcmSendEvent(Object source, DeviceType deviceType, String fcmToken, Long targetCardPk, String requesterNickname, NotificationType notificationType) {
            super(source, deviceType, fcmToken, targetCardPk, requesterNickname);

            if (notificationType.isNotGeneral()) {
                throw new IllegalArgumentException();
            }
            this.notificationType = notificationType;
        }
    }
}
