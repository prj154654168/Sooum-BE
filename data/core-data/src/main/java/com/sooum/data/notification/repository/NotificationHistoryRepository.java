package com.sooum.data.notification.repository;

import com.sooum.data.member.entity.Member;
import com.sooum.data.notification.entity.NotificationHistory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationHistoryRepository extends JpaRepository<NotificationHistory, Long> {

    /**
     *{@code memberPk}가 읽지 않은 시스템 알림(정지, 삭제)을 포함한 전체 알림 개수를 검색합니다.
     * @param memberPk 알림 개수를 조회하는 사용자의 Pk.
     * @return 알림 개수(Long)
     *
     * @since 2024-12-14
     * @author jungwoo
     */
    @Query("select count(n) from NotificationHistory n " +
            "where n.toMember.pk = :memberPk and n.isRead = false ")
    Long findUnreadNotificationCount(@Param("memberPk") Long memberPk);

    /**
     *{@code memberPk}가 읽지 않은 시스템 알림(정지, 삭제)을 제외한 카드 알림 개수를 검색합니다.
     * @param memberPk 알림 개수를 조회하는 사용자의 Pk.
     * @return 알림 개수(Long)
     *
     * @since 2024-12-14
     * @author jungwoo
     */
    @Query("select count(n) from NotificationHistory n " +
            "where n.toMember.pk = :memberPk " +
                "and n.isRead = false " +
                "and n.notificationType = com.sooum.data.notification.entity.notificationtype.NotificationType.COMMENT_WRITE " +
                "and (n.notificationType != com.sooum.data.notification.entity.notificationtype.NotificationType.DELETED " +
                    "or n.notificationType != com.sooum.data.notification.entity.notificationtype.NotificationType.BLOCKED)")
    Long findUnreadCardNotificationCount(@Param("memberPk") Long memberPk);

    /**
     *{@code memberPk}가 읽지 않은 시스템 알림(정지, 삭제)을 제외한 공감 알림 개수를 검색합니다.
     * @param memberPk 알림 개수를 조회하는 사용자의 Pk.
     * @return 알림 개수(Long)
     *
     * @since 2024-12-14
     * @author jungwoo
     */
    @Query("select count(n) from NotificationHistory n " +
            "where n.toMember.pk = :memberPk " +
                "and n.isRead = false " +
                "and (n.notificationType = com.sooum.data.notification.entity.notificationtype.NotificationType.FEED_LIKE " +
                    "or n.notificationType = com.sooum.data.notification.entity.notificationtype.NotificationType.COMMENT_LIKE) " +
                "and (n.notificationType != com.sooum.data.notification.entity.notificationtype.NotificationType.DELETED " +
                    "or n.notificationType != com.sooum.data.notification.entity.notificationtype.NotificationType.BLOCKED)")
    Long findUnreadLikeNotificationCount(@Param("memberPk") Long memberPk);

    /***
     * {@code memberPk}가 받은 시스템 알림(정지, 삭제) 중 읽지 않고 한달이 지나지 않은 모든 알림을 페이징하지 않고 한번에 가져옵니다.
     *
     * @param memberPk 알림을 조회하는 사용자의 Pk.
     * @param minusOneMonths 현재로 부터 한달 전 LocalDateTime.
     * @return {@literal List<NotificationHistory>}
     *
     * @since 2024-12-14
     * @author jungwoo
     */
    @Query("select n from NotificationHistory n " +
            "where n.toMember.pk = :memberPk " +
                "and n.isRead = false " +
                "and (n.notificationType = com.sooum.data.notification.entity.notificationtype.NotificationType.DELETED " +
                    "or n.notificationType = com.sooum.data.notification.entity.notificationtype.NotificationType.BLOCKED) " +
                "and n.createdAt > :minusOneMonths ")
    List<NotificationHistory> findBlockOrDeleteNotifications(@Param("memberPk") Long memberPk, @Param("minusOneMonths") LocalDateTime minusOneMonths);

    /**
     * {@code memberPk}가 읽지 않은 시스템 알림(정지, 삭제)을 제외한 전체 알림을 {@code page}개씩 페이징 조회합니다.
     *
     * @param memberPk 알림을 조회하는 사용자의 Pk.
     * @param lastPk 이전 페이지의 마지막 알림 Pk. 최초 조회는 null을 입력합니다.
     * @param page 조회할 알림 개수.
     * @return {@literal List<Notification>}
     *
     * @since 2024-12-14
     * @author jungwoo
     */
    @Query("select n from NotificationHistory n " +
            "join fetch n.fromMember " +
            "where n.toMember.pk = :memberPk " +
                "and (n.notificationType != com.sooum.data.notification.entity.notificationtype.NotificationType.DELETED " +
                    "or n.notificationType != com.sooum.data.notification.entity.notificationtype.NotificationType.BLOCKED) " +
                "and (:lastPk is null or n.pk < :lastPk) " +
                "and n.isRead = false " +
            "order by n.pk desc ")
    List<NotificationHistory> findUnreadNotifications(@Param("memberPk")Long memberPk, @Param("lastPk") Long lastPk, Pageable page);

    /**
     *{@code memberPk}가 읽은 시스템 알림(정지, 삭제)을 제외한 전체 알림을 {@code page}개씩 페이징 조회합니다.
     *
     * @param memberPk 알림을 조회하는 사용자의 Pk.
     * @param lastPk 이전 페이지의 마지막 알림 Pk. 최초 조회는 null을 입력합니다.
     * @param minusOneDays 현재로 부터 하루 전 LocalDateTime.
     * @param page 조회할 알림 개수.
     * @return {@literal List<NotificationHistory>}
     *
     * @author junwoo
     * @since 2024-12-14
     */
    @Query("select n from NotificationHistory n " +
            "join fetch n.fromMember " +
            "where n.toMember.pk = :memberPk " +
                "and (:lastPk is null or n.pk < :lastPk) " +
                "and (n.notificationType != com.sooum.data.notification.entity.notificationtype.NotificationType.DELETED " +
                    "or n.notificationType != com.sooum.data.notification.entity.notificationtype.NotificationType.BLOCKED) " +
                "and n.isRead = true " +
                "and n.createdAt > :minusOneDays " +
            "order by n.pk desc ")
    List<NotificationHistory> findReadNotifications(@Param("memberPk") Long memberPk, @Param("lastPk") Long lastPk, @Param("minusOneDays") LocalDateTime minusOneDays, Pageable page);

    /**
     * {@code memberPk}가 읽지 않은 시스템 알림(정지, 삭제)을 제외한 카드 알림을 {@code page}개씩 페이징 조회합니다.
     *
     * @param memberPk 알림을 조회하는 사용자의 Pk.
     * @param lastPk 이전 페이지의 마지막 알림 Pk. 최초 조회는 null을 입력합니다.
     * @param page 조회할 알림 개수.
     * @return {@literal List<Notification>}
     *
     * @since 2024-12-14
     * @author jungwoo
     */
    @Query("select n from NotificationHistory n " +
            "join fetch n.fromMember " +
            "where n.toMember.pk = :memberPk " +
                "and (:lastPk is null or n.pk < :lastPk) " +
                "and (n.notificationType != com.sooum.data.notification.entity.notificationtype.NotificationType.DELETED " +
                    "or n.notificationType != com.sooum.data.notification.entity.notificationtype.NotificationType.BLOCKED) " +
                "and n.notificationType = com.sooum.data.notification.entity.notificationtype.NotificationType.COMMENT_WRITE " +
                "and n.isRead = false " +
            "order by n.pk desc ")
    List<NotificationHistory> findUnreadCardNotifications(@Param("memberPk") Long memberPk, @Param("lastPk") Long lastPk, Pageable page);

    /**
     *{@code memberPk}가 읽은 시스템 알림(정지, 삭제)을 제외한 카드 알림을 {@code page}개씩 페이징 조회합니다.
     *
     * @param memberPk 알림을 조회하는 사용자의 Pk.
     * @param lastPk 이전 페이지의 마지막 알림 Pk. 최초 조회는 null을 입력합니다.
     * @param minusOneDays 현재로 부터 하루 전 LocalDateTime.
     * @param page 조회할 알림 개수.
     * @return {@literal List<NotificationHistory>}
     *
     * @author junwoo
     * @since 2024-12-14
     */
    @Query("select n from NotificationHistory n " +
            "join fetch n.fromMember " +
            "where n.toMember.pk = :memberPk " +
                "and (:lastPk is null or n.pk < :lastPk) " +
                "and (n.notificationType != com.sooum.data.notification.entity.notificationtype.NotificationType.DELETED " +
                    "or n.notificationType != com.sooum.data.notification.entity.notificationtype.NotificationType.BLOCKED) " +
                "and n.notificationType = com.sooum.data.notification.entity.notificationtype.NotificationType.COMMENT_WRITE " +
                "and n.isRead = true " +
                "and n.createdAt > :minusOneDays " +
            "order by n.pk desc ")
    List<NotificationHistory> findReadCardNotifications(@Param("memberPk") Long memberPk, @Param("lastPk") Long lastPk, @Param("minusOneDays") LocalDateTime minusOneDays, Pageable page);

    /**
     * {@code memberPk}가 읽지 않은 시스템 알림(정지, 삭제)을 제외한 공감 알림을 {@code page}개씩 페이징 조회합니다.
     *
     * @param memberPk 알림을 조회하는 사용자의 Pk.
     * @param lastPk 이전 페이지의 마지막 알림 Pk. 최초 조회는 null을 입력합니다.
     * @param page 조회할 알림 개수.
     * @return {@literal List<Notification>}
     *
     * @since 2024-12-14
     * @author jungwoo
     */
    @Query("select n from NotificationHistory n " +
            "join fetch n.fromMember " +
            "where n.toMember.pk = :memberPk " +
                "and (:lastPk is null or n.pk < :lastPk) " +
                "and (n.notificationType != com.sooum.data.notification.entity.notificationtype.NotificationType.DELETED " +
                    "or n.notificationType != com.sooum.data.notification.entity.notificationtype.NotificationType.BLOCKED) " +
                "and (n.notificationType = com.sooum.data.notification.entity.notificationtype.NotificationType.FEED_LIKE " +
                    "or n.notificationType = com.sooum.data.notification.entity.notificationtype.NotificationType.COMMENT_LIKE) " +
                "and n.isRead = false " +
            "order by n.pk desc ")
    List<NotificationHistory> findUnreadLikeNotifications(@Param("memberPk") Long memberPk, @Param("lastPk") Long lastPk, Pageable page);

    /**
     *{@code memberPk}가 읽은 시스템 알림(정지, 삭제)을 제외한 공감 알림을 {@code page}개씩 페이징 조회합니다.
     *
     * @param memberPk 알림을 조회하는 사용자의 Pk.
     * @param lastPk 이전 페이지의 마지막 알림 Pk. 최초 조회는 null을 입력합니다.
     * @param minusOneDays 현재로 부터 하루 전 LocalDateTime.
     * @param page 조회할 알림 개수.
     * @return {@literal List<NotificationHistory>}
     *
     * @author junwoo
     * @since 2024-12-14
     */
    @Query("select n from NotificationHistory n " +
            "join fetch n.fromMember " +
            "where n.toMember.pk = :memberPk " +
                "and (:lastPk is null or n.pk < :lastPk) " +
                "and (n.notificationType != com.sooum.data.notification.entity.notificationtype.NotificationType.DELETED " +
                    "or n.notificationType != com.sooum.data.notification.entity.notificationtype.NotificationType.BLOCKED) " +
                "and (n.notificationType = com.sooum.data.notification.entity.notificationtype.NotificationType.FEED_LIKE " +
                    "or n.notificationType = com.sooum.data.notification.entity.notificationtype.NotificationType.COMMENT_LIKE) " +
                "and n.isRead = true " +
                "and n.createdAt > :minusOneDays " +
            "order by n.pk desc ")
    List<NotificationHistory> findReadLikeNotifications(@Param("memberPk") Long memberPk, @Param("lastPk") Long lastPk, @Param("minusOneDays") LocalDateTime minusOneDays, Pageable page);

    @Query("select n.toMember from NotificationHistory n where n.pk = :notificationPk")
    Member findToMember(@Param("notificationPk") Long notificationPk);

    @Transactional
    @Modifying
    @Query("update NotificationHistory n set n.isRead = true where n.pk = :notificationPk")
    void updateToRead(@Param("notificationPk") Long notificationPk);

    @Transactional
    @Modifying
    @Query("delete from NotificationHistory n where n.targetCardPk = :targetCardPk")
    void deleteNotification(@Param("targetCardPk") Long targetCardPk);

    @Transactional
    @Modifying
    @Query("delete from NotificationHistory n " +
            "where n.toMember.pk = :toMemberPk " +
            "and n.notificationType = com.sooum.data.notification.entity.notificationtype.NotificationType.BLOCKED")
    void deletePreviousBlockedHistories(@Param("toMemberPk") Long toMemberPk);
}
