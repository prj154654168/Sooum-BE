package com.sooum.api.notification.service;

import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.AndroidNotification;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.sooum.api.notification.dto.FCMDto;
import com.sooum.data.member.entity.devicetype.DeviceType;
import com.sooum.data.notification.entity.notificationtype.NotificationType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class FCMMsgGenerator {
    @Value("${sooum.server.host}")
    private String host;

    private static final String TITLE = "Sooum";

    public Message generateCommentWriteMsg(FCMDto.GeneralFcm fcmDto) {
        String body = fcmDto.getRequesterNickname() + "님이 답카드를 작성했습니다.";
        return fcmDto.getDeviceType().equals(DeviceType.IOS)
                ? generateGeneralMsgByIos(NotificationType.COMMENT_WRITE, fcmDto.getTargetCardPk(), fcmDto.getFcmToken(), body)
                : generateGeneralMsgByAos(NotificationType.COMMENT_WRITE, fcmDto.getTargetCardPk(), fcmDto.getFcmToken(), body);
    }

    public Message generateFeedLikeMsg(FCMDto.GeneralFcm fcmDto) {
        String body = fcmDto.getRequesterNickname() + "님이 카드에 공감했습니다.";
        return fcmDto.getDeviceType().equals(DeviceType.IOS)
                ? generateGeneralMsgByIos(NotificationType.FEED_LIKE, fcmDto.getTargetCardPk(), fcmDto.getFcmToken(), body)
                : generateGeneralMsgByAos(NotificationType.FEED_LIKE, fcmDto.getTargetCardPk(), fcmDto.getFcmToken(), body);
    }

    public Message generateCommentLikeMsg(FCMDto.GeneralFcm fcmDto) {
        String body = fcmDto.getRequesterNickname() + "님이 카드에 공감했습니다.";
        return fcmDto.getDeviceType().equals(DeviceType.IOS)
                ? generateGeneralMsgByIos(NotificationType.COMMENT_LIKE, fcmDto.getTargetCardPk(), fcmDto.getFcmToken(), body)
                : generateGeneralMsgByAos(NotificationType.COMMENT_LIKE, fcmDto.getTargetCardPk(), fcmDto.getFcmToken(), body);
    }

    public Message generateCardDeletedMsgByReport(FCMDto.SystemFcm fcmDto) {
        String body = "신고로 인해 카드가 삭제 처리됐습니다.";
        return fcmDto.getDeviceType().equals(DeviceType.IOS)
                ? generateSystemMsgByIos(NotificationType.DELETED, fcmDto.getFcmToken(), body)
                : generateSystemMsgByAos(NotificationType.DELETED, fcmDto.getFcmToken(), body);
    }

    public Message generateBlockedMsg(FCMDto.SystemFcm fcmDto) {
        String body = "지속적인 신고로 글쓰기 제한됐습니다.";
        return fcmDto.getDeviceType().equals(DeviceType.IOS)
                ? generateSystemMsgByIos(NotificationType.BLOCKED, fcmDto.getFcmToken(), body)
                : generateSystemMsgByAos(NotificationType.BLOCKED, fcmDto.getFcmToken(), body);
    }

    private Message generateGeneralMsgByAos(NotificationType notificationType, Long targetCardPk, String fcmToken, String body) {
        HashMap<String, String> data = new HashMap<>();
        data.put("link", generateCardDetailUrl(targetCardPk));
        data.put("notificationType", notificationType.name());

        return Message.builder()
                .setAndroidConfig(AndroidConfig.builder()
                        .setNotification(AndroidNotification.builder()
                                .setTitle(TITLE)
                                .setBody(body)
                                .setClickAction(notificationType.toString())
                                .build())
                        .build())
                .putAllData(data)
                .setToken(fcmToken)
                .build();
    }

    private Message generateGeneralMsgByIos(NotificationType notificationType, Long targetCardPk, String fcmToken, String body) {
        HashMap<String, String> data = new HashMap<>();
        data.put("link", generateCardDetailUrl(targetCardPk));
        data.put("notificationType", notificationType.name());

        return Message.builder()
                .setNotification(
                        Notification.builder()
                                .setTitle(TITLE)
                                .setBody(body)
                                .build()
                )
                .putAllData(data)
                .setToken(fcmToken)
                .build();
    }

    private String generateCardDetailUrl(Long targetCardPk) {
        return host + "/" + targetCardPk + "/detail";
    }

    private Message generateSystemMsgByAos(NotificationType notificationType, String fcmToken, String body) {
        return Message.builder()
                .setAndroidConfig(AndroidConfig.builder()
                        .setNotification(AndroidNotification.builder()
                                .setTitle(TITLE)
                                .setBody(body)
                                .setClickAction(notificationType.toString())
                                .build())
                        .build())
                .putData("notificationType", notificationType.name())
                .setToken(fcmToken)
                .build();
    }

    private Message generateSystemMsgByIos(NotificationType notificationType, String fcmToken, String body) {
        return Message.builder()
                .setNotification(
                        Notification.builder()
                                .setTitle(TITLE)
                                .setBody(body)
                                .build()
                )
                .putData("notificationType", notificationType.name())
                .setToken(fcmToken)
                .build();
    }
}
