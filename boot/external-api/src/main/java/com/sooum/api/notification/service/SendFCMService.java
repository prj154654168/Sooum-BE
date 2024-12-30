package com.sooum.api.notification.service;

import com.google.firebase.messaging.Message;
import com.sooum.api.notification.dto.FCMDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SendFCMService {
    private final FCMMsgGenerator fcmMsgGenerator;
    private final FCMSender fcmSender;

    public void sendCommentWriteMsg(FCMDto.GeneralFcm fcmDto) {
        Message message = fcmMsgGenerator.generateGeneralMsg(fcmDto);
        fcmSender.send(message);
    }

    public void sendFeedLikeMsg(FCMDto.GeneralFcm fcmDto) {
        Message message = fcmMsgGenerator.generateGeneralMsg(fcmDto);
        fcmSender.send(message);
    }

    public void sendCommentLikeMsg(FCMDto.GeneralFcm fcmDto) {
        Message message = fcmMsgGenerator.generateGeneralMsg(fcmDto);
        fcmSender.send(message);
    }

    public void sendCardDeletedMsgByReport(FCMDto.SystemFcm fcmDto) {
        Message message = fcmMsgGenerator.generateSystemMsg(fcmDto);
        fcmSender.send(message);
    }
    public void sendBlockedMsg(FCMDto.SystemFcm fcmDto) {
        Message message = fcmMsgGenerator.generateSystemMsg(fcmDto);
        fcmSender.send(message);
    }
}
