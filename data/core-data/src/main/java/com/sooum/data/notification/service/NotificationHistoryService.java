package com.sooum.data.notification.service;

import com.sooum.data.member.entity.Member;
import com.sooum.data.notification.entity.NotificationHistory;
import com.sooum.data.notification.repository.NotificationHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationHistoryService {
    private final NotificationHistoryRepository notificationHistoryRepository;

    @Transactional(readOnly = true)
    public Member findToMember(Long notificationPk) {
        return notificationHistoryRepository.findToMember(notificationPk);
    }

    @Transactional(readOnly = true)
    public Long findUnreadNotificationCount(Long memberPk) {
        return notificationHistoryRepository.findUnreadNotificationCount(memberPk);
    }

    @Transactional(readOnly = true)
    public Long findUnreadCardNotificationCount(Long memberPk) {
        return notificationHistoryRepository.findUnreadCardNotificationCount(memberPk);
    }

    @Transactional(readOnly = true)
    public Long findUnreadLikeNotificationCount(Long memberPk) {
        return notificationHistoryRepository.findUnreadLikeNotificationCount(memberPk);
    }

    /**
     * {@code memberPk}가 읽지않은 전체 알림을 조회합니다. 이때 {@code lastPk}가 null이면 최초 조회이므로 읽지 않은 시스템 알림을 포함하여 조회힙니다.
     * <pre>{@code
     *     - lastPk가 null.
     *       Cf.시스템 알림은 최상단에 위치해야 하므로 Stream을 사용하여 시스템 알림 리스트 뒤에 전체 알림 리스트를 더합니다.
     *     return [시스템 알림, 시스템 알림, ...] + [전체 알림, 전체 알림, ...]
     *
     *     - lastPk가 null 아님.
     *     Cf.최초 조회가 아니기 때문에 최상단에 위치해야 할 시스템 알림을 조회할 필요 없음.
     *     return [전체 알림, 전체 알림, ...]
     * }</pre>
     *
     * @param memberPk 알림을 조회하는 사용자의 Pk.
     * @param lastPk 이전 페이지의 마지막 알림 Pk. 최초 조회는 null을 입력합니다.
     * @return {@literal List<NotificationHistory>}
     * @since 2024-12-14
     * @author jungwoo
     */
    @Transactional(readOnly = true)
    public List<NotificationHistory> findUnreadNotifications(Long memberPk, Optional<Long> lastPk) {

        return lastPk.isEmpty()
                ? Stream.of(notificationHistoryRepository.findBlockOrDeleteNotifications(memberPk, LocalDateTime.now().minusMonths(1)),
                        notificationHistoryRepository.findUnreadNotifications(memberPk, null, PageRequest.ofSize(30)))
                .flatMap(Collection::stream)
                .toList()
                : notificationHistoryRepository.findUnreadNotifications(memberPk, lastPk.orElse(null), PageRequest.ofSize(30));
    }

    @Transactional(readOnly = true)
    public List<NotificationHistory> findReadNotifications(Long memberPk, Optional<Long> lastPk) {
        return notificationHistoryRepository.findReadNotifications(memberPk, lastPk.orElse(null), LocalDateTime.now().minusDays(1), PageRequest.ofSize(30));
    }

    @Transactional(readOnly = true)
    public List<NotificationHistory> findUnreadCardNotifications(Long memberPk, Optional<Long> lastPk) {
        return notificationHistoryRepository.findUnreadCardNotifications(memberPk,lastPk.orElse(null),PageRequest.ofSize(30));
    }

    @Transactional(readOnly = true)
    public List<NotificationHistory> findReadCardNotifications(Long memberPk, Optional<Long> lastPk) {
        return notificationHistoryRepository.findReadCardNotifications(memberPk, lastPk.orElse(null), LocalDateTime.now().minusDays(1), PageRequest.ofSize(30));
    }

    @Transactional(readOnly = true)
    public List<NotificationHistory> findUnreadLikeNotifications(Long memberPk, Optional<Long> lastPk) {
        return notificationHistoryRepository.findUnreadLikeNotifications(memberPk, lastPk.orElse(null), PageRequest.ofSize(30));
    }

    @Transactional(readOnly = true)
    public List<NotificationHistory> findReadLikeNotifications(Long memberPk, Optional<Long> lastPk) {
        return notificationHistoryRepository.findReadLikeNotifications(memberPk, lastPk.orElse(null), LocalDateTime.now().minusDays(1), PageRequest.ofSize(30));
    }

    @Transactional
    public void updateToRead(Long notificationPk) {
        notificationHistoryRepository.updateToRead(notificationPk);
    }

    public void deleteNotification(Long targetCardPk) {
        notificationHistoryRepository.deleteNotification(targetCardPk);
    }
}
