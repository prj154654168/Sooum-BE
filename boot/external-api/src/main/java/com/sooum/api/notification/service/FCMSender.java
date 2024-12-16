package com.sooum.api.notification.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FCMSender {
    public void send(Message message) {
        try {
            String response = FirebaseMessaging.getInstance().send(message);
            log.info("fcm response: {}", response);
        } catch (FirebaseMessagingException e) {
            log.error(e.getMessage());
        }
    }
}
