package com.sooum.api.notification.service;

import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.AndroidNotification;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.sooum.api.notification.dto.FCMDto;
import com.sooum.data.member.entity.devicetype.DeviceType;
import com.sooum.data.notification.entity.notificationtype.NotificationType;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
class FCMMsgGenerator {
    private static final String TITLE = "Sooum";
    private static final String COMMENT_WRITE_SUFFIX = "님이 답카드를 작성했습니다.";
    private static final String LIKE_SUFFIX = "님이 카드에 공감했습니다.";
    private static final String BLOCKED_BODY = "지속적인 신고로 글쓰기 제한됐습니다.";
    private static final String DELETED_BODY = "신고로 인해 카드가 삭제 처리됐습니다.";

    public Message generateGeneralMsg(FCMDto.GeneralFcm fcmDto) {
        return fcmDto.getTargetDeviceType().equals(DeviceType.IOS)
                ? generateGeneralMsgByIos(fcmDto)
                : generateGeneralMsgByAos(fcmDto);
    }

    public Message generateSystemMsg(FCMDto.SystemFcm fcmDto) {
        return fcmDto.getTargetDeviceType().equals(DeviceType.IOS)
                ? generateSystemMsgByIos(fcmDto)
                : generateSystemMsgByAos(fcmDto);
    }

    private Message generateGeneralMsgByAos(FCMDto.GeneralFcm fcmDto) {
        HashMap<String, String> data = generateGeneralFcmData(fcmDto.getNotificationId(), fcmDto.getTargetCardPk(), fcmDto.getNotificationType());

        return Message.builder()
                .setNotification(
                        Notification.builder()
                                .setTitle(TITLE)
                                .setBody(generateGeneralMsgBody(fcmDto.getRequesterNickname(), fcmDto.getNotificationType()))
                                .build()
                )
                .setAndroidConfig(AndroidConfig.builder()
                        .setNotification(AndroidNotification.builder()
                                .setClickAction(fcmDto.getNotificationType().name())
                                .build())
                        .build()
                )
                .putAllData(data)
                .setToken(fcmDto.getTargetFcmToken())
                .build();
    }

    private Message generateGeneralMsgByIos(FCMDto.GeneralFcm fcmDto) {
        HashMap<String, String> data = generateGeneralFcmData(fcmDto.getNotificationId(), fcmDto.getTargetCardPk(), fcmDto.getNotificationType());

        return Message.builder()
                .setNotification(
                        Notification.builder()
                                .setTitle(TITLE)
                                .setBody(generateGeneralMsgBody(fcmDto.getRequesterNickname(), fcmDto.getNotificationType()))
                                .build()
                )
                .putAllData(data)
                .setToken(fcmDto.getTargetFcmToken())
                .build();
    }

    private static HashMap<String, String> generateGeneralFcmData(Long notificationId, Long targetCardPk, NotificationType notificationType) {
        HashMap<String, String> data = new HashMap<>();
        data.put("notificationId", notificationId.toString());
        data.put("targetCardId", targetCardPk.toString());
        data.put("notificationType", notificationType.name());
        return data;
    }

    private static String generateGeneralMsgBody(String requesterName, NotificationType notificationType) {
        return requesterName +
                switch (notificationType) {
            case COMMENT_WRITE -> COMMENT_WRITE_SUFFIX;
            case FEED_LIKE, COMMENT_LIKE -> LIKE_SUFFIX;
            default -> throw new IllegalArgumentException();
        };
    }

    private Message generateSystemMsgByAos(FCMDto.SystemFcm fcmDto) {
        HashMap<String, String> data = generateSystemFcmData(fcmDto.getNotificationId(), fcmDto.getNotificationType());

        return Message.builder()
                .setNotification(
                        Notification.builder()
                                .setTitle(TITLE)
                                .setBody(generateSystemMsgBody(fcmDto.getNotificationType()))
                                .build()
                )
                .setAndroidConfig(AndroidConfig.builder()
                        .setNotification(AndroidNotification.builder()
                                .setClickAction(fcmDto.getNotificationType().name())
                                .build())
                        .build()
                )
                .putAllData(data)
                .setToken(fcmDto.getTargetFcmToken())
                .build();
    }

    private Message generateSystemMsgByIos(FCMDto.SystemFcm fcmDto) {
        HashMap<String, String> data = generateSystemFcmData(fcmDto.getNotificationId(), fcmDto.getNotificationType());

        return Message.builder()
                .setNotification(
                        Notification.builder()
                                .setTitle(TITLE)
                                .setBody(generateSystemMsgBody(fcmDto.getNotificationType()))
                                .build()
                )
                .putAllData(data)
                .setToken(fcmDto.getTargetFcmToken())
                .build();
    }

    private static HashMap<String, String> generateSystemFcmData(Long notificationId, NotificationType notificationType) {
        HashMap<String, String> data = new HashMap<>();
        data.put("notificationId", notificationId.toString());
        data.put("targetCardId", null);
        data.put("notificationType", notificationType.name());
        return data;
    }

    private static String generateSystemMsgBody(NotificationType notificationType) {
        return switch (notificationType) {
            case BLOCKED -> BLOCKED_BODY;
            case DELETED -> DELETED_BODY;
            default -> throw new IllegalArgumentException();
        };
    }
}
