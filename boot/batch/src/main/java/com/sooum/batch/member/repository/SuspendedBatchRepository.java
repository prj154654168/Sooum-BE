package com.sooum.batch.member.repository;

import com.sooum.data.suspended.repository.SuspendedRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

public interface SuspendedBatchRepository extends SuspendedRepository {
    @Modifying
    @Transactional
    @Query("delete from Suspended s where s.untilBan < :now")
    void deleteExpiredSuspensions(@Param("now") LocalDateTime now);
}
