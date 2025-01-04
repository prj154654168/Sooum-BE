package com.sooum.batch.card.batch.repository;

import com.sooum.data.notification.entity.NotificationHistory;
import com.sooum.data.notification.repository.NotificationHistoryRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationBatchRepository extends NotificationHistoryRepository {
    @Query("select n from NotificationHistory n where n.targetCardPk in :targetCardPks")
    List<NotificationHistory> findNotificationsForDeletion(@Param("targetCardPks") List<Long> targetCardPks);
}
