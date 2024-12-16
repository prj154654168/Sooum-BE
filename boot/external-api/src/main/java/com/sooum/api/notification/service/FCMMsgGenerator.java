package com.sooum.api.notification.service;

import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.AndroidNotification;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
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

    public Message generateCommentWriteMsg(DeviceType deviceType, Long targetCardPk, String fcmToken, String writerNickname) {
        String body = writerNickname + "님이 답카드를 작성했습니다.";
        return deviceType.equals(DeviceType.IOS)
                ? generateMsgByIosWithDetailLink(NotificationType.COMMENT_WRITE, targetCardPk, fcmToken, body)
                : generateMsgByAosWithDetailLink(NotificationType.COMMENT_WRITE, targetCardPk, fcmToken, body);
    }

    public Message generateFeedLikeMsg(DeviceType deviceType, Long targetCardPk, String fcmToken, String writerNickname) {
        String body = writerNickname + "님이 카드에 공감했습니다.";
        return deviceType.equals(DeviceType.IOS)
                ? generateMsgByIosWithDetailLink(NotificationType.FEED_LIKE, targetCardPk, fcmToken, body)
                : generateMsgByAosWithDetailLink(NotificationType.FEED_LIKE, targetCardPk, fcmToken, body);
    }

    public Message generateCommentLikeMsg(DeviceType deviceType, Long targetCardPk, String fcmToken, String writerNickname) {
        String body = writerNickname + "님이 카드에 공감했습니다.";
        return deviceType.equals(DeviceType.IOS)
                ? generateMsgByIosWithDetailLink(NotificationType.COMMENT_LIKE, targetCardPk, fcmToken, body)
                : generateMsgByAosWithDetailLink(NotificationType.COMMENT_LIKE, targetCardPk, fcmToken, body);
    }

    public Message generateCardDeletedByReportMsg(DeviceType deviceType, String fcmToken) {
        String body = "신고로 인해 카드가 삭제 처리됐습니다.";
        return deviceType.equals(DeviceType.IOS)
                ? generateMsgByIosWithoutDetailLink(NotificationType.DELETED, fcmToken, body)
                : generateMsgByAosWithoutDetailLink(NotificationType.DELETED, fcmToken, body);
    }

    public Message generateBlockedMsg(DeviceType deviceType, String fcmToken) {
        String body = "지속적인 신고로 글쓰기 제한됐습니다.";
        return deviceType.equals(DeviceType.IOS)
                ? generateMsgByIosWithoutDetailLink(NotificationType.BLOCKED, fcmToken, body)
                : generateMsgByAosWithoutDetailLink(NotificationType.BLOCKED, fcmToken, body);
    }

    private Message generateMsgByAosWithDetailLink(NotificationType notificationType, Long targetCardPk, String fcmToken, String body) {
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

    private Message generateMsgByIosWithDetailLink(NotificationType notificationType, Long targetCardPk, String fcmToken, String body) {
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

    private Message generateMsgByAosWithoutDetailLink(NotificationType notificationType, String fcmToken, String body) {
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

    private Message generateMsgByIosWithoutDetailLink(NotificationType notificationType, String fcmToken, String body) {
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
