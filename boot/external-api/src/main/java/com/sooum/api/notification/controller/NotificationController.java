package com.sooum.api.notification.controller;

import com.sooum.api.notification.dto.NotificationDto;
import com.sooum.api.notification.service.NotificationUseCase;
import com.sooum.global.auth.annotation.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notifications")
public class NotificationController {
    private final NotificationUseCase notificationUseCase;

    @PatchMapping("/{notificationPk}/read")
    public ResponseEntity<?> updateReadStatus(@CurrentUser Long memberPk,
                                              @PathVariable(value = "notificationPk") Long notificationPk) {
        notificationUseCase.setNotificationToRead(memberPk, notificationPk);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/unread-cnt")
    public ResponseEntity<?> findUnreadNotificationCnt(@CurrentUser Long memberPk) {
        return ResponseEntity.ok(notificationUseCase.findUnreadNotificationCntResponse(memberPk));
    }

    @GetMapping("/card/unread-cnt")
    public ResponseEntity<?> findUnreadCardNotificationCnt(@CurrentUser Long memberPk) {
        return ResponseEntity.ok(notificationUseCase.findUnreadCardNotificationCntResponse(memberPk));
    }

    @GetMapping("/like/unread-cnt")
    public ResponseEntity<?> findUnreadLikeNotificationCnt(@CurrentUser Long memberPk) {
        return ResponseEntity.ok(notificationUseCase.findUnreadLikeNotificationCntResponse(memberPk));
    }

    @GetMapping({"/unread", "/unread/{lastId}"})
    public ResponseEntity<?> findUnreadNotifications(@CurrentUser Long memberPk,
                                                     @PathVariable(required = false, value = "lastId") Optional<Long> lastId) {
        List<NotificationDto.CommonNotificationInfo> unreadNotificationResponses = notificationUseCase.findUnreadNotificationResponses(memberPk, lastId);
        if (unreadNotificationResponses.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(unreadNotificationResponses);
    }

    @GetMapping({"/card/unread", "/card/unread/{lastId}"})
    public ResponseEntity<?> findUnreadCardNotifications(@CurrentUser Long memberPk,
                                                         @PathVariable(required = false, value = "lastId") Optional<Long> lastId) {
        List<NotificationDto.CommonNotificationInfo> unreadCardNotificationResponses = notificationUseCase.findUnreadCardNotificationResponses(memberPk, lastId);
        if (unreadCardNotificationResponses.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(unreadCardNotificationResponses);
    }

    @GetMapping({"/like/unread", "/like/unread/{lastId}"})
    public ResponseEntity<?> findUnreadLikeNotifications(@CurrentUser Long memberPk,
                                                         @PathVariable(required = false, value = "lastId") Optional<Long> lastId) {
        List<NotificationDto.CommonNotificationInfo> unreadLikeNotificationResponses = notificationUseCase.findUnreadLikeNotificationResponses(memberPk, lastId);
        if (unreadLikeNotificationResponses.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(unreadLikeNotificationResponses);
    }

    @GetMapping({"/read", "/read/{lastId}"})
    public ResponseEntity<?> findReadNotifications(@CurrentUser Long memberPk,
                                                   @PathVariable(required = false, value = "lastId") Optional<Long> lastId) {
        List<NotificationDto.CommonNotificationInfo> readNotificationResponses = notificationUseCase.findReadNotificationResponses(memberPk, lastId);
        if (readNotificationResponses.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(readNotificationResponses);
    }

    @GetMapping({"/card/read", "/card/read/{lastId}"})
    public ResponseEntity<?> findReadCardNotifications(@CurrentUser Long memberPk,
                                                       @PathVariable(required = false, value = "lastId") Optional<Long> lastId) {
        List<NotificationDto.CommonNotificationInfo> readCardNotificationResponses = notificationUseCase.findReadCardNotificationResponses(memberPk, lastId);
        if (readCardNotificationResponses.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(readCardNotificationResponses);
    }

    @GetMapping({"/like/read", "/like/read/{lastId}"})
    public ResponseEntity<?> findReadLikeNotifications(@CurrentUser Long memberPk,
                                                       @PathVariable(required = false, value = "lastId") Optional<Long> lastId) {
        List<NotificationDto.CommonNotificationInfo> readLikeNotificationResponses = notificationUseCase.findReadLikeNotificationResponses(memberPk, lastId);
        if (readLikeNotificationResponses.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(readLikeNotificationResponses);
    }
}
