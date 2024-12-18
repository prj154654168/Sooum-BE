package com.sooum.api.notification.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MessagingErrorCode;
import com.sooum.api.notification.exception.FcmServerException;
import com.sooum.global.slack.service.SlackService;
import lombok.RequiredArgsConstructor;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class FCMSender {
    private final SlackService slackService;

    @Retryable(retryFor = FcmServerException.class,
            maxAttempts = 3,
            recover = "sendSlackFcmError",
            backoff = @Backoff(delay = 1000, multiplier = 2.0))
    public void send(Message message) {
        try {
            FirebaseMessaging.getInstance().send(message);
        } catch (FirebaseMessagingException e) {
            validateRetrySendFCM(e.getMessagingErrorCode());
        }
    }

    @Recover
    public void sendSlackFcmError(FcmServerException e) {
        slackService.sendSlackFCMMsg();
    }

    private void validateRetrySendFCM(MessagingErrorCode msgErrorCode) {
        if (isRetryErrorCode(msgErrorCode)) {
            throw new FcmServerException();
        }
    }

    private boolean isRetryErrorCode(MessagingErrorCode msgErrorCode) {
        return msgErrorCode.equals(MessagingErrorCode.INTERNAL) || msgErrorCode.equals(MessagingErrorCode.UNAVAILABLE);
    }
}
