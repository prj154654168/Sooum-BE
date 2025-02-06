package com.sooum.api.notification.event;

import com.sooum.api.notification.dto.FCMDto;
import com.sooum.api.notification.service.SendFCMService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class FCMEventListener {
    private final SendFCMService sendFCMService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void sendSystemFCM(FCMDto.SystemFcmSendEvent fcmDto) {
        switch (fcmDto.getNotificationType()) {
            case BLOCKED -> sendFCMService.sendBlockedMsg(fcmDto);
            case DELETED -> sendFCMService.sendCardDeletedMsgByReport(fcmDto);
            case TRANSFER_SUCCESS -> sendFCMService.sendTransferSuccessMsg(fcmDto);
        }
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void sendGeneralFCM(FCMDto.GeneralFcmSendEvent fcmDto) {
        switch (fcmDto.getNotificationType()) {
            case COMMENT_WRITE -> sendFCMService.sendCommentWriteMsg(fcmDto);
            case FEED_LIKE -> sendFCMService.sendFeedLikeMsg(fcmDto);
            case COMMENT_LIKE -> sendFCMService.sendCommentLikeMsg(fcmDto);
        }
    }
}
