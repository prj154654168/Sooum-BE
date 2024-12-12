package com.sooum.data.notification.service;

import com.sooum.data.notification.repository.NotificationHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationHistoryService {
    private final NotificationHistoryRepository notificationHistoryRepository;
}
