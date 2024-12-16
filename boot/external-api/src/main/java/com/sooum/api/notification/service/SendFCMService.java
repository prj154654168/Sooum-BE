package com.sooum.api.notification.service;

import com.google.firebase.messaging.Message;
import com.sooum.data.member.entity.devicetype.DeviceType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SendFCMService {
    private final FCMMsgGenerator fcmMsgGenerator;
    private final FCMSender fcmSender;

    public void sendCommentWriteMsg(DeviceType deviceType, Long targetCardPk, String writerNickname, String fcmToken) {
        Message message = fcmMsgGenerator.generateCommentWriteMsg(deviceType, targetCardPk, fcmToken, writerNickname);
        fcmSender.send(message);
    }

    public void sendFeedLikeMsg(DeviceType deviceType, Long targetCardPk, String writerNickname, String fcmToken) {
        Message message = fcmMsgGenerator.generateFeedLikeMsg(deviceType, targetCardPk, fcmToken, writerNickname);
        fcmSender.send(message);
    }

    public void sendCommentLikeMsg(DeviceType deviceType, Long targetCardPk, String writerNickname, String fcmToken) {
        Message message = fcmMsgGenerator.generateCommentLikeMsg(deviceType, targetCardPk, fcmToken, writerNickname);
        fcmSender.send(message);
    }

    public void sendCardDeletedByReportMsg(DeviceType deviceType, String fcmToken) {
        Message message = fcmMsgGenerator.generateCardDeletedByReportMsg(deviceType, fcmToken);
        fcmSender.send(message);
    }
    public void sendBlockedMsg(DeviceType deviceType, String fcmToken) {
        Message message = fcmMsgGenerator.generateBlockedMsg(deviceType, fcmToken);
        fcmSender.send(message);
    }
}
